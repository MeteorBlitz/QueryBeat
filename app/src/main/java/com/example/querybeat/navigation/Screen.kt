package com.example.querybeat.navigation

sealed class Screen(val route: String) {
    object SchemaExplorer : Screen("schema_explorer")
    object QueryEditor : Screen("query_editor")
    object Results : Screen("results")
    object SavedQueries : Screen("saved_queries")
    object History : Screen("history")
    object Settings : Screen("settings")
    object Country : Screen("country")
}