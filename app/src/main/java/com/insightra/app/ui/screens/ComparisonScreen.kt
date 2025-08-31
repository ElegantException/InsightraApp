package com.insightra.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.insightra.app.viewmodel.ComparisonViewModel
import kotlin.math.floor
import kotlin.math.roundToInt

@Composable
fun ComparisonScreen(
    comparisonId: Long,
    vm: ComparisonViewModel = hiltViewModel()
) {
    val state = vm.state.collectAsState()
    LaunchedEffect(comparisonId) { vm.load(comparisonId) }
    Column {
        if (state.value.loading) {
            Text("Loading...")
        } else if (state.value.error != null) {
            Text(state.value.error ?: "")
        } else {
            LazyColumn {
                item { Text("Comparison Table") }
                items(state.value.tableRows) { row ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(72.dp),
                        colors = CardDefaults.cardColors()
                    ) {
                        Column {
                            Text(row.title)
                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                row.values.forEach { value ->
                                    Text(value)
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }
                item { Text("Ratings") }
                items(state.value.ratings) { r ->
                    RowRating(item = r.item, avg = r.avg, total = r.total)
                }
                item {
                    Spacer(Modifier.height(12.dp))
                    Text("Pros & Cons")
                }
                items(state.value.prosCons) { pc ->
                    Text("${pc.item} Pros")
                    pc.pros.forEach { Text("• $it") }
                    Text("${pc.item} Cons")
                    pc.cons.forEach { Text("• $it") }
                    Spacer(Modifier.height(8.dp))
                }
                item {
                    Text("Recommendations")
                }
                items(state.value.recommendations) { rec ->
                    Text("• $rec")
                }
            }
        }
    }
}

@Composable
private fun RowRating(item: String, avg: Double, total: Int) {
    Column {
        Text(item)
        RowStars(avg)
        Text("Reviews: $total")
    }
}

@Composable
private fun RowStars(avg: Double) {
    val full = floor(avg).toInt()
    val remainder = avg - full
    val half = remainder >= 0.25 && remainder < 0.75
    val plusOne = if (!half && remainder >= 0.75) 1 else 0
    val totalFilled = full + plusOne
    repeat(totalFilled.coerceAtMost(5)) { Icon(Icons.Default.Star, contentDescription = null) }
    val remaining = 5 - totalFilled
    repeat(remaining.coerceAtLeast(0)) { Icon(Icons.Default.Star, contentDescription = null) }
}
