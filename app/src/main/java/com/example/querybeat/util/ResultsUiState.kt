package com.example.querybeat.util

sealed class ResultsUiState {
    object Idle : ResultsUiState()
    object Loading : ResultsUiState()
    data class Success(val data: String) : ResultsUiState()
    data class Error(val message: String) : ResultsUiState()
}