package com.example.querybeat.screen.country

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.querybeat.data.repository.country.CountryRepository
import com.example.querybeat.util.CountryUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class CountryViewModel @Inject constructor(
    private val repository: CountryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<CountryUiState>(CountryUiState.Loading)
    val uiState: StateFlow<CountryUiState> = _uiState

    init {
        fetchCountries()
    }

    private fun fetchCountries() {
        viewModelScope.launch {
            try {
                val countries = repository.getCountries()
                _uiState.value = CountryUiState.Success(countries)
            } catch (e: Exception) {
                _uiState.value = CountryUiState.Error("Something went wrong")
            }
        }
    }
}