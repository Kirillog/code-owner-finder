package org.intellij.sdk.plugin.toolWindow

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.openapi.wm.ToolWindowManager

class MyToolWindowFactory : ToolWindowFactory {

    /**
     * Create the tool window content.
     *
     * @param project    current project
     * @param toolWindow current tool window
     */
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val myToolWindowContent = ToolWindowContentBuilder(toolWindow)
            .addDefaultMessage()
            .buildContent()
        toolWindow.contentManager.addContent(myToolWindowContent)
    }

    companion object {
        private const val ID = "Code Owner"

        /**
         * Return tool window with [ID] of current [project]
         */

        fun getToolWindowOf(project: Project): ToolWindow? {
            val toolWindowManager = ToolWindowManager.getInstance(project)
            return toolWindowManager.getToolWindow(ID)
        }
    }
}
