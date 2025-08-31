package com.insightra.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.insightra.app.data.prefs.SavedKeyInfo
import com.insightra.app.data.prefs.SettingsRepository
import com.insightra.app.network.OpenAIService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import okhttp3.internal.toLongOrDefault

data class SavedInfoState(val date: String)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repo: SettingsRepository,
    private val api: OpenAIService
) : ViewModel() {
    private var _savedInfo: SavedInfoState? = null
    val savedInfo: SavedInfoState? get() = _savedInfo

    init {
        viewModelScope.launch {
            val info: SavedKeyInfo? = repo.getSavedInfo()
            _savedInfo = info?.let { SavedInfoState(it.date) }
        }
    }

    fun verifyAndSave(key: String, onError: (String) -> Unit, onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (key.isBlank()) {
                onError("API key is required")
                return@launch
            }
            try {
                val resp = api.listModels("Bearer $key")
                if (resp.isSuccessful) {
                    repo.setApiKey(key)
                    val info = repo.getSavedInfo()
                    _savedInfo = info?.let { SavedInfoState(it.date) }
                    onSuccess()
                } else {
                    onError("Invalid API key")
                }
            } catch (e: Exception) {
                onError("Verification failed")
            }
        }
    }
}
