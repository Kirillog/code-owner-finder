package org.intellij.sdk.plugin.toolWindow

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.ToolWindow
import com.intellij.ui.content.Content
import com.intellij.ui.content.ContentFactory
import org.intellij.sdk.plugin.SummaryContribution
import org.intellij.sdk.plugin.VCSManager
import org.intellij.sdk.plugin.estimator.TimeEditorEstimator
import java.awt.Color
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingConstants


class ToolWindowContentBuilder(private val toolWindow: ToolWindow) {

    private val content: JPanel = JPanel()
    private val constraints = GridBagConstraints()

    init {
        content.layout = GridBagLayout()
        constraints.fill = GridBagConstraints.HORIZONTAL
        constraints.anchor = GridBagConstraints.NORTH
        constraints.gridy = 0
    }

    fun addDefaultMessage(): ToolWindowContentBuilder {
        val label = JLabel("Select file to see info")
        label.verticalAlignment = SwingConstants.CENTER
        label.horizontalAlignment = SwingConstants.CENTER
        content.add(label, constraints)
        constraints.gridy++
        return this
    }

    /**
     * Adds [data] to [content] of toolWindow
     */

    fun add(data: List<SummaryContribution>): ToolWindowContentBuilder {
        val toolWindowTable = ToolWindowTable(data).content
        constraints.weighty = 1.0
        constraints.weightx = 1.0
        constraints.gridheight = GridBagConstraints.RELATIVE
        content.add(toolWindowTable, constraints)
        constraints.gridy++
        return this
    }

    fun add(codeOwner : String) : ToolWindowContentBuilder {
        val label = JLabel("Predicted Code Owner: $codeOwner")
        label.foreground = Color.GREEN
        constraints.weighty = 0.0
        constraints.weightx = 1.0
        content.add(label, constraints)
        constraints.gridy++
        return this
    }

    fun buildContent(): Content {
        val contentFactory = ContentFactory.SERVICE.getInstance()
        return contentFactory.createContent(content, "", false)
    }

}

fun reloadContent(project: Project, file: VirtualFile) {
    val toolWindow = MyToolWindowFactory.getToolWindowOf(project) ?: return
    toolWindow.contentManager.removeAllContents(true)
    val singleContributionList = VCSManager(project).authorContributionFor(file)
    val summaryContributionList = VCSManager.singleContributionsToSummary(singleContributionList)
    val scores = TimeEditorEstimator().estimateContributors(singleContributionList)
    summaryContributionList.forEach {
        it.score = scores[it.author] ?: 0.0
    }
    val codeOwner = scores.maxByOrNull { it.value }?.key ?: "there is no authors"
    val toolWindowContent = ToolWindowContentBuilder(toolWindow)
        .add(codeOwner)
        .add(summaryContributionList)
        .buildContent()
    toolWindow.contentManager.addContent(toolWindowContent)
}