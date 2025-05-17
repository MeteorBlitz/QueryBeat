package com.example.querybeat.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.querybeat.screen.country.CountryScreen

@Composable
fun AppNavHost(navController: NavHostController, startDestination: String = Screen.Country.route) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.Country.route) {
            CountryScreen()
        }
        // Add more composable routes here
    }
}