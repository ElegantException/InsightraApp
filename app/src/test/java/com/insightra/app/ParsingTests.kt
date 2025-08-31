package com.insightra.app

import com.insightra.app.network.model.ComparisonAttributeRow
import com.insightra.app.network.model.ComparisonResultPayload
import com.insightra.app.network.model.ItemRating
import com.insightra.app.network.model.ProsCons
import com.insightra.app.network.model.Recommendation
import com.insightra.app.network.model.VisionExtractResult
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class ParsingTests {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun parseVisionExtractResult() {
        val s = """{"brand":"Acme","name":"Ultra Widget","size":"2 pack"}"""
        val r = json.decodeFromString(VisionExtractResult.serializer(), s)
        assertEquals("Acme", r.brand)
        assertEquals("Ultra Widget", r.name)
        assertEquals("2 pack", r.size)
    }

    @Test
    fun parseComparisonResultPayload() {
        val payload = ComparisonResultPayload(
            table = listOf(
                ComparisonAttributeRow("Price", listOf("$10", "$12")),
                ComparisonAttributeRow("Weight", listOf("1kg", "1.2kg"))
            ),
            ratings = listOf(
                ItemRating(0, 4.5, 1200),
                ItemRating(1, 4.2, 800)
            ),
            prosCons = listOf(
                ProsCons(0, listOf("Durable","Affordable","Warranty"), listOf("Limited colors","Bulky","Noisy")),
                ProsCons(1, listOf("Lightweight","Quiet","Compact"), listOf("Expensive","Fragile","Short warranty"))
            ),
            recommendations = listOf(
                Recommendation("Choose item 1 for budget value"),
                Recommendation("Choose item 2 for portability")
            )
        )
        val s = json.encodeToString(payload)
        val back = json.decodeFromString(ComparisonResultPayload.serializer(), s)
        assertEquals(2, back.table.size)
        assertEquals(2, back.ratings.size)
        assertEquals(2, back.prosCons.size)
        assertEquals(2, back.recommendations.size)
    }
}
