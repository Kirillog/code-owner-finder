package org.intellij.sdk.plugin

import java.util.*

/**
 * Represents changes between two revisions of file
 * [added], [deleted], [changed] are numbers of added, deleted and changed lines respectively
 */

data class EditedLines(val added: Int, val deleted: Int, val changed: Int) {
    operator fun plus(other: EditedLines): EditedLines =
        EditedLines(added + other.added, deleted + other.deleted, changed + other.changed)

    fun summary(): Int =
        added + deleted + changed

}

/**
 * Represents contribution of [author], including [numberOfEditedLines] and [date] of revision
 */

data class SingleContribution(val author: String, val numberOfEditedLines: EditedLines, val date: Date)

/**
 * Represents summary contribution of [author], including [numberOfRevisions], [numberOfEditedLines] and estimated [score]
 */

data class SummaryContribution(
    val author: String,
    val numberOfRevisions: Int,
    val numberOfEditedLines: EditedLines,
    val lastRevisionDate: Date,
    var score: Double = 0.0
) {
    constructor(contribution: SingleContribution) : this(
        contribution.author,
        1,
        contribution.numberOfEditedLines,
        contribution.date
    )
}


fun max(date1: Date, date2: Date): Date =
    if (date1.after(date2))
        date1
    else
        date2
