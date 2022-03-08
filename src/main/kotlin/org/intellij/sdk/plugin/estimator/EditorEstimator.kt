package org.intellij.sdk.plugin.estimator

import org.intellij.sdk.plugin.SingleContribution
import org.intellij.sdk.plugin.VCSManager

class EditorEstimator : Estimator {
    override fun estimate(contribution: List<SingleContribution>): AuthorsScore {
        val summaryContribution = VCSManager.singleContributionsToSummary(contribution)
        return summaryContribution.associate {
            it.author to it.numberOfEditedLines.summary().toDouble()
        }
    }

    override fun normalizeScore(authorsScore: AuthorsScore): AuthorsScore = authorsScore

}