package org.intellij.sdk.plugin.estimator

import org.intellij.sdk.plugin.SingleContribution
import java.time.Duration

class TimeEditorEstimator : Estimator() {

    override fun estimateFunction(
        partScore: Double,
        contribution: SingleContribution
    ): Double  {
        val time = Duration.between(contribution.date.toInstant(), currentDate.toInstant())
        return partScore + contribution.numberOfEditedLines.summary().toDouble() / kotlin.math.max(
            time.toDays(),
            1
        )
    }
}