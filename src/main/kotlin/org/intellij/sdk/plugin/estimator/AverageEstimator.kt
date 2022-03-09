package org.intellij.sdk.plugin.estimator

import org.intellij.sdk.plugin.SingleContribution

class AverageEstimator : Estimator() {

    private val estimatorList = listOf(TimeEditorEstimator(), EditorEstimator(), TimeEstimator())
    private val ratios = listOf(0.4, 0.4, 0.2)

    override fun estimateFunction(partScore: Double, contribution: SingleContribution): Double = 0.0

    override fun estimateContributors(contribution: List<SingleContribution>): AuthorsScore {
        val authorsScores = estimatorList.map { it.estimateContributors(contribution).toList() }.flatten()
        return authorsScores.groupBy { it.first }.mapValues { (_, scores) ->
            scores.zip(ratios) { score, ratio ->
                score.second * ratio
            }.sum()
        }
    }
}