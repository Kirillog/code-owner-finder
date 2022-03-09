package org.intellij.sdk.plugin.toolWindow

import com.intellij.openapi.wm.ToolWindow
import com.intellij.ui.content.Content
import com.intellij.ui.content.ContentFactory
import org.intellij.sdk.plugin.SummaryContribution
import java.awt.BorderLayout
import java.awt.Color
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingConstants

/**
 * Represents implementation of builder pattern for flexible configuration of [toolWindow].
 *
 * Use [content] to get instance of [JPanel] representing content of [toolWindow].
 */


class ToolWindowContentBuilder(private val toolWindow: ToolWindow) {

    private var content: JPanel = JPanel()

    init {
        content.layout = BorderLayout()
    }

    /**
     * Adds [data] to [content] of toolWindow
     * @param data represents information about contribution of authors. See [SummaryContribution] for details.
     */
    fun add(data: List<SummaryContribution>): ToolWindowContentBuilder {
        val toolWindowTable = ToolWindowTable(data).content
        content.add(toolWindowTable, BorderLayout.CENTER)
        return this
    }

    /**
     * Adds [text] to [content] of toolWindow
     */

    fun add(text: String): ToolWindowContentBuilder {
        val label = JLabel(text)
        label.foreground = Color.LIGHT_GRAY
        label.horizontalAlignment = SwingConstants.CENTER
        content.add(label, BorderLayout.CENTER)
        return this
    }

    /**
     * Returns [content] of [toolWindow]
     * @param name is name of [Content] panel
     */

    fun buildContent(name: String = ""): Content {
        val contentFactory = ContentFactory.SERVICE.getInstance()
        return contentFactory.createContent(content, name, false)
    }

}
