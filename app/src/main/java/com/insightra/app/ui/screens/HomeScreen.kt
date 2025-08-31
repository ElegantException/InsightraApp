package com.insightra.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.insightra.app.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    padding: PaddingValues,
    onStartNew: () -> Unit,
    onOpenComparison: (Long) -> Unit,
    vm: HomeViewModel = hiltViewModel()
) {
    val recent = vm.recent.collectAsState(initial = emptyList())
    Column {
        Button(onClick = onStartNew) {
            Text("Start New Comparison")
        }
        LazyColumn {
            items(recent.value) { item ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenComparison(item.id) }
                ) {
                    Text("${item.itemCount} Products Compared on ${item.date}")
                    Divider()
                }
            }
        }
    }
}
