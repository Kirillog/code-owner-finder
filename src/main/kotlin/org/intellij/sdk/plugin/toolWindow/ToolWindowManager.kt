package org.intellij.sdk.plugin.toolWindow

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowManager
import org.intellij.sdk.plugin.VCSManager

object ToolWindowManager {

    private const val ID = "Code Owner"

    /**
     * Main function, reloading content in [toolWindow] to display new info about contributions.
     *
     * Invoke this function on appropriate events.
     */

    fun reloadContent(project: Project, file: VirtualFile) {
        val toolWindow = getToolWindowOf(project) ?: return
        toolWindow.contentManager.removeAllContents(true)
        val summaryContributionList = VCSManager.summaryContributions(project, file)
        val codeOwner = summaryContributionList.maxByOrNull { it.score }?.author ?: "there is no authors"
        val toolWindowContent = ToolWindowContentBuilder(toolWindow)
            .add(summaryContributionList)
            .buildContent(codeOwner)
        toolWindow.contentManager.addContent(toolWindowContent)
    }

    fun show(project : Project) {
        val toolWindow = getToolWindowOf(project) ?: return
        toolWindow.show()
    }


    /**
     * Return tool window with [ID] of current [project]
     */

    private fun getToolWindowOf(project: Project): ToolWindow? {
        val toolWindowManager = ToolWindowManager.getInstance(project)
        return toolWindowManager.getToolWindow(ID)
    }
}