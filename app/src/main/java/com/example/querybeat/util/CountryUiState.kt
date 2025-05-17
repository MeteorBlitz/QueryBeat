package com.example.querybeat.util

import com.example.querybeat.data.model.Country

sealed class CountryUiState {
    object Loading : CountryUiState()
    data class Success(val countries: List<Country>) : CountryUiState()
    data class Error(val message: String) : CountryUiState()
}
