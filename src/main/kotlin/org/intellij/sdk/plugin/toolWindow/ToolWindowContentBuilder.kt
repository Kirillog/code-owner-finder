package org.intellij.sdk.plugin.toolWindow

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.ToolWindow
import com.intellij.ui.content.Content
import com.intellij.ui.content.ContentFactory
import org.intellij.sdk.plugin.SummaryContribution
import org.intellij.sdk.plugin.VCSManager
import java.awt.GridLayout
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingConstants

class ToolWindowContentBuilder(private val toolWindow: ToolWindow) {

    private val content: JPanel = JPanel()
    init {
        content.layout = GridLayout()
    }

    fun addDefaultMessage(): ToolWindowContentBuilder {
        val label = JLabel("Select file to see info")
        label.verticalAlignment = SwingConstants.CENTER
        label.horizontalAlignment = SwingConstants.CENTER
        content.add(label)
        return this
    }

    /**
     * Adds [data] to [content] of toolWindow
     */

    fun add(data: List<SummaryContribution>): ToolWindowContentBuilder {
        val toolWindowTable = ToolWindowTable(data)
        content.add(toolWindowTable.content)
        return this
    }

    fun buildContent(): Content {
        val contentFactory = ContentFactory.SERVICE.getInstance()
        return contentFactory.createContent(content, "", false)
    }

}

fun reloadContent(project: Project, file: VirtualFile) {
    val toolWindow = MyToolWindowFactory.getToolWindowOf(project) ?: return
    val contributionInformation = VCSManager(project).summaryContributionFor(file)
    toolWindow.contentManager.removeAllContents(true)
    val toolWindowContent = ToolWindowContentBuilder(toolWindow)
        .add(contributionInformation)
        .buildContent()
    toolWindow.contentManager.addContent(toolWindowContent)
}