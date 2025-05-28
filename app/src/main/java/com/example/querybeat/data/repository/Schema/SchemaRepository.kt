package com.example.querybeat.data.repository.Schema

interface SchemaRepository {
    suspend fun loadSchemaFromAssets(): String
}
