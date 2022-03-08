package org.intellij.sdk.plugin.estimator

import org.intellij.sdk.plugin.SingleContribution

typealias AuthorsScore = Map<String, Double>

interface Estimator {

    fun estimate(contribution: List<SingleContribution>): AuthorsScore

    fun estimateContributors(contribution: List<SingleContribution>) =
        normalizeScore(estimate(contribution))


    fun normalizeScore(authorsScore: AuthorsScore) : AuthorsScore
}