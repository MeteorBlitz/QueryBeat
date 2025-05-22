package com.example.querybeat.screen.country

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.querybeat.components.BaseScreen
import com.example.querybeat.util.CountryUiState

@Composable
fun CountryScreen(navController: NavController,
                  currentRoute: String,
                  drawerState: DrawerState,
                  viewModel: CountryViewModel = hiltViewModel()) {
    val uiStateState = viewModel.uiState.collectAsState()
    val uiState = uiStateState.value

    BaseScreen(
        navController = navController,
        currentRoute = currentRoute,
        drawerState = drawerState,
        title = "Countries",
    ){paddingValues ->
        when (uiState) {
            is CountryUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is CountryUiState.Success -> {
                val countries = uiState.countries
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    items(countries) { country ->
                        Text(
                            "${country.name} ${country.emoji}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        HorizontalDivider()
                    }
                }
            }

            is CountryUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(uiState.message)
                }
            }
        }
    }


}

