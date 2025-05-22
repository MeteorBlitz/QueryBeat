package com.example.querybeat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.querybeat.navigation.AppNavigation
import com.example.querybeat.ui.theme.QueryBeatTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QueryBeatTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    QueryBeatAppUI()
                }
            }
        }
    }
}

@Composable
fun QueryBeatAppUI() {
    AppNavigation()
}