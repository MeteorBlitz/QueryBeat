package com.example.querybeat.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseScreen(
    title: String? = null,
    showTopBar: Boolean = true,
    showBottomBar: Boolean = false,
    bottomBarContent: @Composable (() -> Unit)? = null,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            if (showTopBar && title != null) {
                TopAppBar(title = { Text(title) })
            }
        },
        bottomBar = {
            if (showBottomBar && bottomBarContent != null) {
                bottomBarContent()
            }
        },
        content = content
    )
}
