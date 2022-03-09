package org.intellij.sdk.plugin

import com.intellij.openapi.project.Project
import com.intellij.openapi.vcs.ProjectLevelVcsManager
import com.intellij.openapi.vcs.actions.VcsContextFactory
import com.intellij.openapi.vcs.history.VcsFileRevision
import com.intellij.openapi.vfs.VirtualFile
import dev.gitlive.difflib.DiffUtils
import dev.gitlive.difflib.patch.DeltaType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.intellij.sdk.plugin.estimator.AverageEstimator

class VCSManager(private val project: Project) {

    /**
     * Returns list of [SingleContribution] related to this [file]
     */

    fun authorContributionFor(file: VirtualFile): List<SingleContribution> {
        val vcsManager = ProjectLevelVcsManager.getInstance(project)
        val contextFactory = VcsContextFactory.SERVICE.getInstance()
        val revisionList = runBlocking(Dispatchers.IO) {
            vcsManager.allActiveVcss.map { vcs ->
                val history = vcs.vcsHistoryProvider?.createSessionFor(contextFactory.createFilePathOn(file))
                (history?.revisionList ?: listOf())
            }
        }.flatten() + VcsFileRevision.NULL
        return authorContributions(revisionList, file)
    }

    companion object {

        /**
         * Converts [contribution] list of [SingleContribution] to list of [SummaryContribution]
         */
        fun singleContributionsToSummary(contribution: List<SingleContribution>) =
            contribution.groupingBy { it.author }
                .aggregate { key, accumulator: SummaryContribution?, element, _ ->
                    if (accumulator == null) {
                        SummaryContribution(element)
                    } else {
                        SummaryContribution(
                            key,
                            accumulator.numberOfRevisions + 1,
                            accumulator.numberOfEditedLines + element.numberOfEditedLines,
                            max(accumulator.lastRevisionDate, element.date)
                        )
                    }
                }.values.toList()

        /**
         * Returns [SummaryContribution] of all authors, committing to [file] in this [project]
         */

        fun summaryContributions(
            project: Project,
            file: VirtualFile
        ): List<SummaryContribution> {
            val singleContributionList = VCSManager(project).authorContributionFor(file)
            val summaryContributionList = singleContributionsToSummary(singleContributionList)
            val scores = AverageEstimator().estimateContributors(singleContributionList)
            summaryContributionList.forEach {
                it.score = scores[it.author] ?: 0.0
            }
            return summaryContributionList
        }

    }

    /**
     * Converts [revisionList] of [file] to list of [SingleContribution]
     */

    private fun authorContributions(
        revisionList: List<VcsFileRevision>,
        file: VirtualFile
    ) = revisionList.reversed().zipWithNext { oldRevision, newRevision ->
        val oldContent = oldRevision.loadContent().toString(file.charset).split("\n")
        val newContent = newRevision.loadContent().toString(file.charset).split("\n")
        val numberOfEditedLines = editedLines(oldContent, newContent)
        SingleContribution(newRevision.author ?: "no author", numberOfEditedLines, newRevision.revisionDate)
    }

    /**
     * Forms [EditedLines] instance comparing [oldContent] and [newContent], representing content of file
     */

    private fun editedLines(oldContent: List<String>, newContent: List<String>): EditedLines {
        val patch = DiffUtils.diff(oldContent, newContent)
        return patch.getDeltas().fold(EditedLines(0, 0, 0)) { part, delta ->
            val amount = delta.target.size()
            when (delta.type) {
                DeltaType.INSERT ->
                    EditedLines(part.added + amount, part.deleted, part.changed)
                DeltaType.DELETE ->
                    EditedLines(part.added, part.deleted + delta.source.size(), part.changed)
                DeltaType.CHANGE ->
                    EditedLines(part.added, part.deleted, part.changed + amount)
                else ->
                    part
            }
        }
    }
}