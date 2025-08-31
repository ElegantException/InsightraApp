package com.insightra.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.insightra.app.data.db.InsightraDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class RecentComparisonUi(val id: Long, val itemCount: Int, val date: String)

@HiltViewModel
class HomeViewModel @Inject constructor(
    db: InsightraDatabase
) : ViewModel() {
    private val comparisonDao = db.comparisonDao()
    private val _recent = MutableStateFlow<List<RecentComparisonUi>>(emptyList())
    val recent: StateFlow<List<RecentComparisonUi>> = _recent

    init {
        viewModelScope.launch {
            comparisonDao.recent().collectLatest { list ->
                _recent.value = list.map { RecentComparisonUi(it.id, it.itemCount, it.createdAt.toString()) }
            }
        }
    }
}
