package com.insightra.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.insightra.app.data.db.InsightraDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.runBlocking

data class TableRow(val title: String, val values: List<String>)
data class RatingRow(val item: String, val avg: Double, val total: Int)
data class ProsCons(val item: String, val pros: List<String>, val cons: List<String>)
data class ComparisonUiState(
    val tableRows: List<TableRow>,
    val ratings: List<RatingRow>,
    val prosCons: List<ProsCons>,
    val recommendations: List<String>
)

@HiltViewModel
class ComparisonViewModel @Inject constructor(
    private val db: InsightraDatabase
) : ViewModel() {
    fun load(id: Long): ComparisonUiState = runBlocking {
        // Placeholder until AI integration wired
        ComparisonUiState(
            tableRows = listOf(
                TableRow("Brand", listOf("Item 1", "Item 2"))
            ),
            ratings = listOf(
                RatingRow("Item 1", 4.5, 1234),
                RatingRow("Item 2", 4.2, 987)
            ),
            prosCons = listOf(
                ProsCons("Item 1", listOf("Pro A", "Pro B", "Pro C"), listOf("Con A", "Con B", "Con C")),
                ProsCons("Item 2", listOf("Pro A", "Pro B", "Pro C"), listOf("Con A", "Con B", "Con C"))
            ),
            recommendations = listOf("Choose Item 1 if you want X", "Choose Item 2 if you prefer Y")
        )
    }
}
