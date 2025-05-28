package com.example.querybeat.data.repository.Schema

import android.app.Application
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SchemaRepositoryImpl @Inject constructor(
    private val application: Application
) : SchemaRepository {

    companion object {
        private const val SCHEMA_FILE_NAME = "display_schema.txt"
    }

    override suspend fun loadSchemaFromAssets(): String {
        return withContext(Dispatchers.IO) {
            application.assets.open(SCHEMA_FILE_NAME)
                .bufferedReader()
                .use { it.readText() }
        }
    }
}
