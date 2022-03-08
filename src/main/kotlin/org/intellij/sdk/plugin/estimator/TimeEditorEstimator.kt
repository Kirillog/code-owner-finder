package org.intellij.sdk.plugin.estimator

import org.intellij.sdk.plugin.SingleContribution
import java.time.Duration
import java.util.*

class TimeEditorEstimator : Estimator {
    override fun estimate(contribution: List<SingleContribution>): AuthorsScore {
        return contribution.groupBy { it.author }.mapValues { (_, contributions) ->
            val currentDate = Date()
            val score = contributions.fold(0.0) { partScore, contribution ->
                val time = Duration.between(contribution.date.toInstant(), currentDate.toInstant())
                partScore + contribution.numberOfEditedLines.summary().toDouble() / kotlin.math.max(
                    time.toDays(),
                    1
                )
            }
            score
        }
    }

    override fun normalizeScore(authorsScore: AuthorsScore): AuthorsScore {
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