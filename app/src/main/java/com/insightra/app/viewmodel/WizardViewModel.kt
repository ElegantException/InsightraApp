package com.insightra.app.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.insightra.app.data.db.ComparisonEntity
import com.insightra.app.data.db.InsightraDatabase
import com.insightra.app.data.db.ItemEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

data class WizardItem(var imageUri: String, var brand: String, var name: String, var size: String)
data class WizardState(val items: MutableList<WizardItem> = mutableListOf()) {
    val canSaveCurrent: Boolean get() = true
}

@HiltViewModel
class WizardViewModel @Inject constructor(
    private val db: InsightraDatabase
) : ViewModel() {
    val state = WizardState()

    fun takePhoto() {
        // stub
    }

    fun pickFromGallery() {
        // stub
    }

    fun updateBrand(index: Int, v: String) { state.items[index].brand = v }
    fun updateName(index: Int, v: String) { state.items[index].name = v }
    fun updateSize(index: Int, v: String) { state.items[index].size = v }

    fun saveCurrentItem() {
        // stub for UI flow
    }

    fun compare(onReady: (Long) -> Unit) {
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
                        size = it.size
                    )
                )
            }
            onReady(id)
        }
    }
}
