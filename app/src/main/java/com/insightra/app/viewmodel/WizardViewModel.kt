package com.insightra.app.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.insightra.app.data.db.ComparisonEntity
import com.insightra.app.data.db.InsightraDatabase
import com.insightra.app.data.db.ItemEntity
import com.insightra.app.network.OpenAIRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

data class WizardItem(var imageUri: String, var brand: String, var name: String, var size: String)
data class WizardState(val items: MutableList<WizardItem> = mutableListOf()) {
    val canSaveCurrent: Boolean get() = items.size in 0..2
    val canCompare: Boolean get() = items.size >= 2
}

@HiltViewModel
class WizardViewModel @Inject constructor(
    private val db: InsightraDatabase,
    private val ai: OpenAIRepository,
    private val settings: com.insightra.app.data.prefs.SettingsRepository
) : ViewModel() {
    val state = WizardState()

    fun addImage(uri: Uri) {
        if (state.items.size >= 3) return
        viewModelScope.launch {
            val key = settings.getApiKey() ?: return@launch
            val parsed = ai.extractFromImage(key, uri)
            state.items.add(
                WizardItem(
                    imageUri = uri.toString(),
                    brand = parsed.brand,
                    name = parsed.name,
                    size = parsed.size.ifBlank { "1 unit" }
                )
            )
        }
    }

    fun removeAt(index: Int) {
        if (index in state.items.indices) state.items.removeAt(index)
    }

    fun updateBrand(index: Int, v: String) { state.items[index].brand = v }
    fun updateName(index: Int, v: String) { state.items[index].name = v }
    fun updateSize(index: Int, v: String) { state.items[index].size = v }

    fun compare(onReady: (Long) -> Unit) {
        if (!state.canCompare) return
        viewModelScope.launch {
            val id = db.comparisonDao().insertComparison(
                ComparisonEntity(createdAt = System.currentTimeMillis(), itemCount = state.items.size)
            )
            state.items.forEach {
                db.itemDao().insertItem(
                    ItemEntity(
                        comparisonId = id,
                        imageUri = it.imageUri,
                        brand = it.brand,
                        name = it.name,
                        size = it.size.ifBlank { "1 unit" }
                    )
                )
            }
            onReady(id)
        }
    }
}
