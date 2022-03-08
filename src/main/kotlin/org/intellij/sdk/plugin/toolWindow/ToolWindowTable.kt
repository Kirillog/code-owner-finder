package org.intellij.sdk.plugin.toolWindow

import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.table.TableView
import com.intellij.util.ui.ColumnInfo
import com.intellij.util.ui.ListTableModel
import org.intellij.sdk.plugin.SummaryContribution
import javax.swing.JPanel

class ToolWindowTable(data : List<SummaryContribution>) {
    private val modelList: ListTableModel<SummaryContribution> = ListTableModel()
    private val tableView: TableView<SummaryContribution> = TableView()
    val content : JPanel

    init {
        modelList.columnInfos = arrayOf(
            AuthorsColumn(), CommitColumn(),
            AddedLinesColumn(), DeletedLinesColumn(), ChangedLinesColumn()
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
    override fun valueOf(item: SummaryContribution): String {
        return item.author
    }
}

class CommitColumn : ColumnInfo<SummaryContribution, String>("Commits") {
    override fun valueOf(item: SummaryContribution): String {
        return item.numberOfRevisions.toString()
    }
}

class AddedLinesColumn : ColumnInfo<SummaryContribution, String>("Added Lines") {
    override fun valueOf(item: SummaryContribution): String {
        return item.numberOfEditedLines.added.toString()
    }
}

class ChangedLinesColumn : ColumnInfo<SummaryContribution, String>("Changed Lines") {
    override fun valueOf(item: SummaryContribution): String {
        return item.numberOfEditedLines.changed.toString()
    }
}

class DeletedLinesColumn : ColumnInfo<SummaryContribution, String>("Deleted Lines") {
    override fun valueOf(item: SummaryContribution): String {
        return item.numberOfEditedLines.deleted.toString()
    }
}