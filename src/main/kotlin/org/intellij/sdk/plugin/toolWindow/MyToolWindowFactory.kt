package org.intellij.sdk.plugin.toolWindow

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory

class MyToolWindowFactory : ToolWindowFactory {

    /**
     * Create the tool window content.
     *
     * @param project    current project
     * @param toolWindow current tool window
     */
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val myToolWindowContent = ToolWindowContentBuilder(toolWindow)
            .add("Select file to see code owner")
            .buildContent()
        toolWindow.contentManager.addContent(myToolWindowContent)
    }

    companion object {

    }
}
