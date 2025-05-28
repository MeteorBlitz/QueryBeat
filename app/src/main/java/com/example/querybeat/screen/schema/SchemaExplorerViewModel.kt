package com.example.querybeat.screen.schema

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.querybeat.util.SchemaUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import javax.inject.Inject

@HiltViewModel
class SchemaExplorerViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {

    companion object {
        private const val SCHEMA_FILE_NAME = "display_schema.txt"
    }

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

                val schemaContent = withContext(Dispatchers.IO) { // Schema loading on IO thread
                    getApplication<Application>().assets.open(SCHEMA_FILE_NAME)
                        .bufferedReader().use(BufferedReader::readText)
                }

                cachedSchema = schemaContent
                _uiState.value = SchemaUiState.Success(schemaContent)
            } catch (e: Exception) {
                _uiState.value = SchemaUiState.Error("Failed to load schema: ${e.message ?: "Unknown error"}")
                e.printStackTrace()
            }
        }
    }
}

