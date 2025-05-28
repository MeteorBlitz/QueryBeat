package com.example.querybeat.screen.results

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.querybeat.data.repository.results.ResultsRepository
import com.example.querybeat.util.ResultsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ResultsViewModel @Inject constructor(private val repository: ResultsRepository) : ViewModel() {

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
            val res = repository.runQuery(
                query,
                variablesJsonString,
                operationName,
                rootOperationTypeName
            )
            if (res.isSuccess) {
                val data = res.getOrNull() ?: ""
                _result.value = ResultsUiState.Success(data)
            } else {
                val errorMsg = res.exceptionOrNull()?.message ?: "Unknown error"
                _result.value = ResultsUiState.Error(errorMsg)
            }


        }
    }
}