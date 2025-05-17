package com.example.querybeat.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.querybeat.screen.country.CountryScreen
import com.example.querybeat.screen.editor.QueryEditorScreen

@Composable
fun AppNavHost(navController: NavHostController, startDestination: String = Screen.Country.route) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.Country.route) {
            CountryScreen(navController = navController)
        }
        composable(Screen.QueryEditor.route) {
            QueryEditorScreen(
                onRunQuery = { query ->
                    // Later: Navigate to Results screen with query
                    println("User Query: $query")
                }
            )
        }
    }
}