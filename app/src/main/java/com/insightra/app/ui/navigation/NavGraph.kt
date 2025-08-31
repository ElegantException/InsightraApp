package com.insightra.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.insightra.app.ui.screens.ComparisonScreen
import com.insightra.app.ui.screens.HomeScreen
import com.insightra.app.ui.screens.SettingsScreen
import com.insightra.app.ui.screens.WizardScreen

object Routes {
    const val Home = "home"
    const val Settings = "settings"
    const val Wizard = "wizard"
    const val Comparison = "comparison/{comparisonId}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightraNavHost() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val route = backStackEntry?.destination?.route ?: Routes.Home

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Insightra") },
                actions = {
                    if (route != Routes.Settings) {
                        IconButton(onClick = { navController.navigate(Routes.Settings) }) {
                            Icon(Icons.Default.Settings, contentDescription = "Settings")
                        }
                    }
                }
            )
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.Home
        ) {
            composable(Routes.Home) {
                HomeScreen(
                    padding = padding,
                    onStartNew = { navController.navigate(Routes.Wizard) },
                    onOpenComparison = { id -> navController.navigate("comparison/$id") },
                )
            }
            composable(Routes.Settings) {
                SettingsScreen(padding = padding)
            }
            composable(Routes.Wizard) {
                WizardScreen(
                    onComparisonReady = { id -> navController.navigate("comparison/$id") }
                )
            }
            composable(Routes.Comparison) { entry ->
                val id = entry.arguments?.getString("comparisonId")?.toLongOrNull() ?: 0L
                ComparisonScreen(comparisonId = id)
            }
        }
    }
}
