import org.intellij.sdk.plugin.EditedLines
import org.intellij.sdk.plugin.SingleContribution
import org.intellij.sdk.plugin.SummaryContribution
import org.intellij.sdk.plugin.VCSManager
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.*
import kotlin.test.assertEquals

internal class VCSManagerTest {
    private val date = LocalDate.now()
    private val dates = List(4) { Date.from(date.minusDays(it.toLong()).atStartOfDay().toInstant(ZoneOffset.UTC)) }
    private val singleContribution = List(4) {
        SingleContribution(
            "author${it % 2}",
            EditedLines(2 * it, 0, it),
            dates[it]
        )
    }

    @Test
    fun convertingFromSingleToSummaryTest() {
        assertEquals(listOf(
            SummaryContribution("author0",
                2,
                EditedLines(4, 0, 2),
                dates[0]
            ),
            SummaryContribution("author1",
            2,
            EditedLines(8, 0, 4),
            dates[1])
        ), VCSManager.singleContributionsToSummary(singleContribution))
    }
}