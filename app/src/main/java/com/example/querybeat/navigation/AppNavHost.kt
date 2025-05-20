package com.example.querybeat.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.querybeat.screen.country.CountryScreen
import com.example.querybeat.screen.editor.QueryEditorScreen
import com.example.querybeat.screen.results.ResultsScreen

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
                    navController.navigate(Screen.Results.route + "?query=${Uri.encode(query)}")
                    println("User Query: $query")
                }
            )
        }
        composable(
            route = Screen.Results.route + "?query={query}",
            arguments = listOf(navArgument("query") { type = NavType.StringType })
        ) { backStackEntry ->
            val query = backStackEntry.arguments?.getString("query") ?: ""
            ResultsScreen(query = query, navController = navController)
        }
    }
}