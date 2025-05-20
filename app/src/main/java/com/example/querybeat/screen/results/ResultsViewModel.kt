package com.example.querybeat.screen.results

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.querybeat.data.remote.GraphQLClient
import com.example.querybeat.graphql.RawStringQuery
import com.example.querybeat.util.ResultsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject // Ensure this is the correct import for @Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

@HiltViewModel
class ResultsViewModel @Inject constructor() : ViewModel() {

    private val _result = MutableStateFlow<ResultsUiState>(ResultsUiState.Idle)
    val result: StateFlow<ResultsUiState> = _result

    fun runQuery(
        query: String,
        variablesJsonString: String? = null,
        operationName: String = "RawGraphQLQuery",
        rootOperationTypeName: String = "Query"
    ) {
        viewModelScope.launch {
            _result.value = ResultsUiState.Loading
            try {
                val variablesMap: Map<String, JsonElement> = if (!variablesJsonString.isNullOrBlank()) {
                    Log.d("ResultsViewModel", "Decoding variables: $variablesJsonString")
                    Json.decodeFromString<JsonObject>(variablesJsonString).toMap()
                } else {
                    emptyMap()
                }

                Log.d("ResultsViewModel", "About to create RawStringQuery")
                val rawQuery = RawStringQuery(
                    queryString = query,
                    operationNameString = operationName,
                    queryVariables = variablesMap,
                    rootOperationSchemaTypeName = rootOperationTypeName
                )
                Log.d("ResultsViewModel", "RawStringQuery created")

                Log.d("ResultsViewModel", "Executing query:\n$query")
                val response = GraphQLClient.apolloClient.query(rawQuery).execute()

                // --- START OF MODIFICATIONS ---

                // Log the full ApolloResponse object and its exception property
                Log.d("ResultsViewModel", "ApolloResponse received: $response")
                if (response.exception != null) {
                    Log.e("ResultsViewModel", "Apollo Network/Execution Exception: ${response.exception?.message}", response.exception)
                    _result.value = ResultsUiState.Error("Network Error: ${response.exception?.message}")
                    return@launch // Exit early if there's a low-level network/execution exception
                }

                // Log the raw data and errors directly from the response
                Log.d("ResultsViewModel", "ApolloResponse.data (before rawData): ${response.data}")
                Log.d("ResultsViewModel", "ApolloResponse.errors (raw): ${response.errors}")


                // Check for GraphQL errors (server-side errors, even if HTTP status is 200)
                if (!response.errors.isNullOrEmpty()) {
                    val errorMessage = response.errors?.joinToString("\n") { it.message } ?: "Unknown GraphQL error"
                    Log.e("ResultsViewModel", "GraphQL Errors: $errorMessage")
                    _result.value = ResultsUiState.Error("GraphQL Errors: $errorMessage")
                    return@launch // Exit early if GraphQL errors are present
                }

                // If no exceptions or GraphQL errors, then proceed to process data
                val dataString = response.data?.rawData?.toString() ?: "No data returned (response.data or rawData was null)"
                Log.d("ResultsViewModel", "Query response: $dataString")

                _result.value = ResultsUiState.Success(dataString)

                // --- END OF MODIFICATIONS ---

            } catch (e: Exception) {
                // This catch block handles any other unexpected exceptions during the process
                Log.e("ResultsViewModel", "Catch-all Query error: ${e.message}", e)
                _result.value = ResultsUiState.Error(e.message ?: "Unknown error: ${e.javaClass.simpleName}")
            }
        }
    }
}