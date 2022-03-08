package org.intellij.sdk.plugin

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.Project
import org.intellij.sdk.plugin.toolWindow.reloadContent


class CodeOwnerFinderAction : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        val file = event.getRequiredData(CommonDataKeys.VIRTUAL_FILE)
        val project: Project = event.getRequiredData(CommonDataKeys.PROJECT)
        reloadContent(project, file)
    }

    override fun update(event: AnActionEvent) {
        val project = event.project
        val file = event.getData(CommonDataKeys.VIRTUAL_FILE)
        event.presentation.isEnabledAndVisible = project != null && file != null && !file.isDirectory
    }
}
