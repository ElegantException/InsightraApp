package com.insightra.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.insightra.app.data.db.ComparisonResultEntity
import com.insightra.app.data.db.InsightraDatabase
import com.insightra.app.network.OpenAIRepository
import com.insightra.app.network.model.ComparisonAttributeRow
import com.insightra.app.network.model.ComparisonResultPayload
import com.insightra.app.network.model.ItemRating
import com.insightra.app.network.model.ProsCons
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

data class TableRow(val title: String, val values: List<String>)
data class RatingRow(val item: String, val avg: Double, val total: Int)
data class ComparisonUiState(
    val loading: Boolean = true,
    val tableRows: List<TableRow> = emptyList(),
    val ratings: List<RatingRow> = emptyList(),
    val prosCons: List<com.insightra.app.viewmodel.ProsCons> = emptyList(),
    val recommendations: List<String> = emptyList(),
    val error: String? = null
)

data class ProsCons(val item: String, val pros: List<String>, val cons: List<String>)

@HiltViewModel
class ComparisonViewModel @Inject constructor(
    private val db: InsightraDatabase,
    private val ai: OpenAIRepository,
    private val settings: com.insightra.app.data.prefs.SettingsRepository
) : ViewModel() {
    private val _state = MutableStateFlow(ComparisonUiState())
    val state: StateFlow<ComparisonUiState> = _state.asStateFlow()

    fun load(comparisonId: Long) {
        _state.value = ComparisonUiState(loading = true)
        viewModelScope.launch {
            val cached = db.resultDao().resultForComparison(comparisonId)
            if (cached != null) {
                _state.value = toUiState(cached, comparisonId)
                return@launch
            }
            try {
                val items = db.itemDao().itemsForComparison(comparisonId)
                val key = settings.getApiKey() ?: run {
                    _state.value = ComparisonUiState(loading = false, error = "API key not set")
                    return@launch
                }
                val payload: ComparisonResultPayload = ai.generateComparison(key, items)
                val entity = ComparisonResultEntity(
                    comparisonId = comparisonId,
                    tableJson = Json.encodeToString(payload.table),
                    ratingsJson = Json.encodeToString(payload.ratings),
                    prosConsJson = Json.encodeToString(payload.prosCons),
                    recommendationsJson = Json.encodeToString(payload.recommendations)
                )
                db.resultDao().insertResult(entity)
                _state.value = toUiState(entity, comparisonId)
            } catch (e: Exception) {
                _state.value = ComparisonUiState(loading = false, error = "Failed to generate comparison")
            }
        }
    }

    private fun toUiState(entity: ComparisonResultEntity, comparisonId: Long): ComparisonUiState {
        val table: List<ComparisonAttributeRow> = Json.decodeFromString(entity.tableJson)
        val ratings: List<ItemRating> = Json.decodeFromString(entity.ratingsJson)
        val prosCons: List<ProsCons> = Json.decodeFromString(entity.prosConsJson)
        val recs: List<com.insightra.app.network.model.Recommendation> = Json.decodeFromString(entity.recommendationsJson)
        val items = runCatching { db.itemDao().itemsForComparison(comparisonId) }.getOrNull().orEmpty()
        val tableRows = table.map { TableRow(it.attribute, it.values) }
        val ratingRows = ratings.map { r ->
            val name = items.getOrNull(r.itemIndex)?.name ?: "Item ${r.itemIndex + 1}"
            RatingRow(name, r.averageOutOf5, r.totalReviews)
        }
        val pcRows = prosCons.map { pc ->
            val name = items.getOrNull(pc.itemIndex)?.name ?: "Item ${pc.itemIndex + 1}"
            com.insightra.app.viewmodel.ProsCons(name, pc.pros, pc.cons)
        }
        return ComparisonUiState(
            loading = false,
            tableRows = tableRows,
            ratings = ratingRows,
            prosCons = pcRows,
            recommendations = recs.map { it.text },
            error = null
        )
    }
}
