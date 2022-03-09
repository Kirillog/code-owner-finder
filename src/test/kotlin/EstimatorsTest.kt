import org.intellij.sdk.plugin.EditedLines
import org.intellij.sdk.plugin.SingleContribution
import org.intellij.sdk.plugin.estimator.AverageEstimator
import org.intellij.sdk.plugin.estimator.EditorEstimator
import org.intellij.sdk.plugin.estimator.TimeEditorEstimator
import org.intellij.sdk.plugin.estimator.TimeEstimator
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.*
import kotlin.test.assertEquals


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class EstimatorsTest {
    private val date = LocalDate.now()
    private val firstDate: Date = Date.from(date.minusDays(200).atStartOfDay().toInstant(ZoneOffset.UTC))
    private val secondDate = Date.from(date.minusDays(100).atStartOfDay().toInstant(ZoneOffset.UTC))
    val defaultContribution1 = listOf(
        SingleContribution(
            "test_author",
            EditedLines(100, 50, 50),
            firstDate
        ),
        SingleContribution(
            "test2_author",
            EditedLines(50, 0, 50),
            secondDate
        )
    )
    private val defaultContribution2 = listOf(
        SingleContribution(
            "test_author",
            EditedLines(100, 50, 50),
            firstDate
        ),
        SingleContribution(
            "test2_author",
            EditedLines(50, 0, 50),
            firstDate
        )
    )

    @Nested
    inner class TimeEditorEstimatorTest {
        private val estimator = TimeEditorEstimator()

        @Test
        fun equalContribution() {
            val authorsScore = estimator.estimateContributors(defaultContribution1)
            assertEquals(mapOf("test_author" to 1.0, "test2_author" to 1.0), authorsScore)
        }

        @Test
        fun uppNormalize() {
            val authorsScore = estimator.estimateContributors(
                listOf(
                    SingleContribution(
                        "test_author",
                        EditedLines(10, 5, 5),
                        firstDate
                    ),
                    SingleContribution(
                        "test2_author",
                        EditedLines(5, 0, 5),
                        secondDate
                    )
                )
            )
            assertEquals(mapOf("test_author" to 1.6, "test2_author" to 1.6), authorsScore)
        }

        @Test
        fun downNormalize() {
            val authorsScore = estimator.estimateContributors(
                listOf(
                    SingleContribution(
                        "test_author",
                        EditedLines(1000, 500, 500),
                        firstDate
                    ),
                    SingleContribution(
                        "test2_author",
                        EditedLines(500, 0, 500),
                        secondDate
                    )
                )
            )
            assertEquals(mapOf("test_author" to 1.25, "test2_author" to 1.25), authorsScore)
        }
    }

    @Nested
    inner class EditorEstimatorTest {
        private val estimator = EditorEstimator()

        @Test
        fun simple() {
            val authorsScore = estimator.estimateContributors(defaultContribution1)
            assertEquals(mapOf("test_author" to 3.125, "test2_author" to 1.5625), authorsScore)
        }

        @Test
        fun equalContribution() {
            val authorsScore = estimator.estimateContributors(
                listOf(
                    SingleContribution(
                        "test_author",
                        EditedLines(100, 20, 80),
                        firstDate
                    ),
                    SingleContribution(
                        "test2_author",
                        EditedLines(50, 140, 10),
                        secondDate
                    )
                )
            )
            assertEquals(mapOf("test_author" to 1.5625, "test2_author" to 1.5625), authorsScore)
        }
    }

    @Nested
    inner class TimeEstimatorTest {
        private val estimator = TimeEstimator()

        @Test
        fun simple() {
            val authorsScore = estimator.estimateContributors(defaultContribution1)
            assertEquals(mapOf("test_author" to 1.28, "test2_author" to 2.56), authorsScore)
        }

        @Test
        fun equalContribution() {
            val authorsScore = estimator.estimateContributors(defaultContribution2)
            assertEquals(mapOf("test_author" to 1.28, "test2_author" to 1.28), authorsScore)
        }
    }


    @Nested
    inner class AverageEstimatorTest {
        private val estimator = AverageEstimator()

        @Test
        fun simple() {
            val authorsScore = estimator.estimateContributors(defaultContribution1)
            assertEquals(mapOf("test_author" to 1.906, "test2_author" to 1.537), authorsScore)
        }

        @Test
        fun equalContribution() {
            val authorsScore = estimator.estimateContributors(listOf(
                SingleContribution(
                    "test_author",
                    EditedLines(100, 50, 50),
                    firstDate
                ),
                SingleContribution(
                    "test2_author",
                    EditedLines(100, 50, 50),
                    firstDate
                )
            ))
            assertEquals(mapOf("test_author" to 1.281, "test2_author" to 1.281), authorsScore)
        }
    }

}