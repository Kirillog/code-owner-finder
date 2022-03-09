package org.intellij.sdk.plugin.estimator

import org.intellij.sdk.plugin.SingleContribution
import java.util.*

typealias AuthorsScore = Map<String, Double>

/**
 * Represents estimator of contribution of authors,
 * providing [estimateFunction] abstract function for overriding in its inheritors.
 *
 * Use [estimateContributors] for getting list of [AuthorsScore].
 */

abstract class Estimator {

    protected val currentDate = Date()

    /**
     * Estimates [contribution] of each author and returns [AuthorsScore]
     */

    private fun estimate(contribution: List<SingleContribution>): AuthorsScore =
         contribution.groupBy { it.author }.mapValues { (_, contributions) ->
            contributions.fold(0.0, ::estimateFunction) }

    /**
     * Returns score after adding [contribution] to existing [partScore]
     */

    abstract fun estimateFunction(partScore : Double, contribution : SingleContribution) : Double

    open fun estimateContributors(contribution: List<SingleContribution>) =
        normalizeScore(estimate(contribution))

    /**
     * Normalizes values of scores so that they will be in `[1.0, 2.0`]
     */

    private fun normalizeScore(authorsScore: AuthorsScore) : AuthorsScore {
        val notZeros = authorsScore.filter { it.value != 0.0 }
        return if (notZeros.isNotEmpty()) {
            val minimum = notZeros.minOf { it.value }
            var delta = 1.0
            while (minimum * delta < 1)
                delta *= 2
            while (minimum * delta > 2)
                delta /= 2
            authorsScore.mapValues {
                it.value * delta
            }
        } else
            authorsScore
    }
}