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
import java.util.*


data class EditedLines(val added: Int, val deleted: Int, val changed: Int) {
    operator fun plus(other: EditedLines): EditedLines =
        EditedLines(added + other.added, deleted + other.deleted, changed + other.changed)

    fun summary(): Int {
        return added + deleted + changed
    }
}


data class SingleContribution(val author: String, val numberOfEditedLines: EditedLines, val date: Date)

data class SummaryContribution(
    val author: String,
    val numberOfRevisions: Int,
    val numberOfEditedLines: EditedLines,
    var score: Double = 0.0
) {
    constructor(contribution: SingleContribution) : this(contribution.author, 1, contribution.numberOfEditedLines)
}

class VCSManager(private val project: Project) {

    fun authorContributionFor(file: VirtualFile): List<SingleContribution> {
        val vcsManager = ProjectLevelVcsManager.getInstance(project)
        val contextFactory = VcsContextFactory.SERVICE.getInstance()
        val revisionList = runBlocking(Dispatchers.IO) {
            val git = vcsManager.findVcsByName("Git") ?: return@runBlocking emptyList()
            val history = git.vcsHistoryProvider?.createSessionFor(contextFactory.createFilePathOn(file))
            (history?.revisionList ?: listOf())
        } + VcsFileRevision.NULL
        return authorContributions(revisionList, file)
    }

    companion object {
        fun singleContributionsToSummary(contribution: List<SingleContribution>) =
            contribution.groupingBy { it.author }
                .aggregate { key, accumulator: SummaryContribution?, element, _ ->
                    if (accumulator == null) {
                        SummaryContribution(element)
                    } else {
                        SummaryContribution(
                            key,
                            accumulator.numberOfRevisions + 1,
                            accumulator.numberOfEditedLines + element.numberOfEditedLines
                        )
                    }
                }.values.toList()

    }

    fun summaryContributionFor(file: VirtualFile): List<SummaryContribution> =
        singleContributionsToSummary(authorContributionFor(file))

    private fun authorContributions(
        revisionList: List<VcsFileRevision>,
        file: VirtualFile
    ) = revisionList.reversed().zipWithNext { oldRevision, newRevision ->
        val oldContent = oldRevision.loadContent().toString(file.charset).split("\n")
        val newContent = newRevision.loadContent().toString(file.charset).split("\n")
        val numberOfEditedLines = editedLines(oldContent, newContent)
        SingleContribution(newRevision.author ?: "no author", numberOfEditedLines, newRevision.revisionDate)
    }

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