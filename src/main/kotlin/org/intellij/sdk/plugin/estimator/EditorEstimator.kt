package org.intellij.sdk.plugin.estimator

import org.intellij.sdk.plugin.SingleContribution

class EditorEstimator : Estimator() {

    override fun estimateFunction(
        partScore: Double,
        contribution: SingleContribution
    ): Double =
        partScore + contribution.numberOfEditedLines.summary()

}