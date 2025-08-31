package com.insightra.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.insightra.app.data.prefs.SavedKeyInfo
import com.insightra.app.data.prefs.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class SavedInfoState(val date: String)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repo: SettingsRepository
) : ViewModel() {
    private val _savedInfo = MutableStateFlow<SavedInfoState?>(null)
    val savedInfo: SavedInfoState? get() = _savedInfo.value

    init {
        viewModelScope.launch {
            val info: SavedKeyInfo? = repo.getSavedInfo()
            _savedInfo.value = info?.let { SavedInfoState(it.date) }
        }
    }

    fun verifyAndSave(key: String, onError: (String) -> Unit, onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (key.isBlank()) {
                onError("API key is required")
                return@launch
            }
            try {
                repo.setApiKey(key)
                val info = repo.getSavedInfo()
                _savedInfo.value = info?.let { SavedInfoState(it.date) }
                onSuccess()
            } catch (e: Exception) {
                onError("Failed to save key")
            }
        }
    }
}
