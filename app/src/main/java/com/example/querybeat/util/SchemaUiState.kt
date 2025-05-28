package com.example.querybeat.util

sealed class SchemaUiState {
    object Loading : SchemaUiState()
    data class Success(val schemaContent: String) : SchemaUiState()
    data class Error(val message: String) : SchemaUiState()
}