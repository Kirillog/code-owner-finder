package org.intellij.sdk.plugin

import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.Project
import org.intellij.sdk.plugin.toolWindow.reloadContent

class FileEditorManager(private val project: Project) :
    FileEditorManagerListener {

    /**
     * Reloads content of tool window on selectionChanged [event]
     */
    override fun selectionChanged(event: FileEditorManagerEvent) {
        if (event.newFile.isInLocalFileSystem)
            reloadContent(project, event.newFile)
    }
}