package com.insightra.app.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.insightra.app.viewmodel.ComparisonViewModel

@Composable
fun ComparisonScreen(
    comparisonId: Long,
    vm: ComparisonViewModel = hiltViewModel()
) {
    val state = vm.load(comparisonId)
    Column {
        LazyColumn {
            items(state.tableRows) { row ->
                Text(row.title)
                row.values.forEach { value ->
                    Text(value)
                }
            }
            item {
                Text("Ratings")
            }
            items(state.ratings) { r ->
                RowRating(item = r.item, avg = r.avg, total = r.total)
            }
            item { Text("Pros & Cons") }
            items(state.prosCons) { pc ->
                Text("${pc.item} Pros")
                pc.pros.forEach { Text("• $it") }
                Text("${pc.item} Cons")
                pc.cons.forEach { Text("• $it") }
            }
            item {
                Text("Recommendations")
            }
            items(state.recommendations) { rec ->
                Text("• $rec")
            }
        }
    }
}

@Composable
private fun RowRating(item: String, avg: Double, total: Int) {
    Text(item)
    RowStars(avg)
    Text("Reviews: $total")
}

@Composable
private fun RowStars(avg: Double) {
    val full = avg.toInt()
    repeat(full) { Icon(Icons.Default.Star, contentDescription = null) }
}
