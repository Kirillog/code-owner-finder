package org.intellij.sdk.plugin

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.Project
import org.intellij.sdk.plugin.toolWindow.ToolWindowManager


class ShowToolWindowAction : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        val file = event.getRequiredData(CommonDataKeys.VIRTUAL_FILE)
        val project: Project = event.getRequiredData(CommonDataKeys.PROJECT)
        ToolWindowManager.reloadContent(project, file)
        ToolWindowManager.show(project)
    }

    override fun update(event: AnActionEvent) {
        val project = event.project
        val file = event.getData(CommonDataKeys.VIRTUAL_FILE)
        event.presentation.isEnabledAndVisible = project != null && file != null && !file.isDirectory
    }
}
