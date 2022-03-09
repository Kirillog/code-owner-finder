package org.intellij.sdk.plugin.toolWindow

import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.table.TableView
import com.intellij.util.ui.ColumnInfo
import com.intellij.util.ui.ListTableModel
import org.intellij.sdk.plugin.SummaryContribution
import java.text.DateFormat
import javax.swing.JPanel

/**
 * Represents table providing information about author's contribution.
 *
 * Use [content] for getting [JPanel] including this table.
 */
class ToolWindowTable(data: List<SummaryContribution>) {
    private val modelList: ListTableModel<SummaryContribution> = ListTableModel()
    private val tableView: TableView<SummaryContribution> = TableView()
    val content: JPanel

    init {
        modelList.columnInfos = arrayOf(
            AuthorsColumn(), CommitColumn(),
            AddedLinesColumn(), DeletedLinesColumn(), ChangedLinesColumn(),
            LastRevisionDateColumn(), ScoreColumn()
        )
        for (contributionInformation in data)
            modelList.addRow(contributionInformation)
        tableView.setModelAndUpdateColumns(modelList)
        content = ToolbarDecorator.createDecorator(tableView)
            .disableAddAction()
            .disableDownAction()
            .disableRemoveAction()
            .disableUpDownActions()
            .createPanel()
    }

}

class AuthorsColumn : ColumnInfo<SummaryContribution, String>("Author") {
    override fun valueOf(item: SummaryContribution): String =
        item.author
}

class CommitColumn : ColumnInfo<SummaryContribution, String>("Commits") {
    override fun valueOf(item: SummaryContribution): String =
        item.numberOfRevisions.toString()
}

class AddedLinesColumn : ColumnInfo<SummaryContribution, String>("Added Lines") {
    override fun valueOf(item: SummaryContribution): String =
        item.numberOfEditedLines.added.toString()
}

class ChangedLinesColumn : ColumnInfo<SummaryContribution, String>("Changed Lines") {
    override fun valueOf(item: SummaryContribution): String =
        item.numberOfEditedLines.changed.toString()
}

class DeletedLinesColumn : ColumnInfo<SummaryContribution, String>("Deleted Lines") {
    override fun valueOf(item: SummaryContribution): String =
        item.numberOfEditedLines.deleted.toString()
}

class LastRevisionDateColumn : ColumnInfo<SummaryContribution, String>("Last Revision") {
    override fun valueOf(item: SummaryContribution): String =
        DateFormat.getDateInstance().format(item.lastRevisionDate)
}

class ScoreColumn : ColumnInfo<SummaryContribution, String>("Score") {
    override fun valueOf(item: SummaryContribution): String =
        "%.2f".format(item.score)
}