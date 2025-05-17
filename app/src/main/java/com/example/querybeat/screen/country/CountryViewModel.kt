package com.example.querybeat.screen.country

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.exception.ApolloException
import com.example.querybeat.GetCountriesQuery
import com.example.querybeat.data.model.Country
import com.example.querybeat.data.remote.GraphQLClient
import com.example.querybeat.util.CountryUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class CountryViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow<CountryUiState>(CountryUiState.Loading)
    val uiState: StateFlow<CountryUiState> = _uiState

    init {
        fetchCountries()
    }


    private fun fetchCountries() {

        viewModelScope.launch {
            try {
                val response = GraphQLClient.apolloClient.query(GetCountriesQuery()).execute()
                val countries = response.data?.countries?.map {
                    Country(
                        code = it.code,
                        name = it.name,
                        emoji = it.emoji
                    )
                } ?: emptyList()
                _uiState.value = CountryUiState.Success(countries)
            } catch (e: ApolloException) {
                _uiState.value = CountryUiState.Error("Something went wrong")
            }
        }

    }

}