package com.example.querybeat.screen.schema

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.querybeat.data.repository.Schema.SchemaRepository
import com.example.querybeat.util.SchemaUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SchemaExplorerViewModel @Inject constructor(
    private val repository: SchemaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SchemaUiState>(SchemaUiState.Loading)
    val uiState: StateFlow<SchemaUiState> = _uiState

    private var cachedSchema: String? = null

    init {
        loadSchema()
    }

    fun loadSchema() {
        viewModelScope.launch {
            _uiState.value = SchemaUiState.Loading

            try {
                cachedSchema?.let {
                    _uiState.value = SchemaUiState.Success(it)
                    return@launch
                }

                val schemaContent = repository.loadSchemaFromAssets()
                cachedSchema = schemaContent
                _uiState.value = SchemaUiState.Success(schemaContent)

            } catch (e: Exception) {
                _uiState.value = SchemaUiState.Error("Failed to load schema: ${e.message ?: "Unknown error"}")
                e.printStackTrace()
            }
        }
    }
}


