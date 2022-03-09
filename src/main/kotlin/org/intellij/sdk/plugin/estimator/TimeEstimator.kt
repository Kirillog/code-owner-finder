package org.intellij.sdk.plugin.estimator

import org.intellij.sdk.plugin.SingleContribution
import java.time.Duration
import kotlin.math.max

class TimeEstimator : Estimator() {

    override fun estimateFunction(partScore: Double, contribution: SingleContribution): Double {
        val time = Duration.between(contribution.date.toInstant(), currentDate.toInstant())
        return partScore + 1.0 / max(
            time.toDays(),
            1
        )
    }
}