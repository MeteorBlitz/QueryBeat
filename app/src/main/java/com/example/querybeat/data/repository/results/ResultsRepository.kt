package com.example.querybeat.data.repository.results

import com.apollographql.apollo.ApolloClient
import com.example.querybeat.graphql.RawStringQuery
import jakarta.inject.Inject
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

class ResultsRepository @Inject constructor(private val apolloClient: ApolloClient) {
    suspend fun runQuery(
        query: String,
        variablesJsonString: String?,
        operationName: String,
        rootOperationTypeName: String
    ): Result<String> {
        return try {
            val variablesMap = if (!variablesJsonString.isNullOrBlank()) {
                Json.decodeFromString<JsonObject>(variablesJsonString).toMap()
            } else emptyMap()

            val rawQuery = RawStringQuery(query, operationName, variablesMap, rootOperationTypeName)
            val response = apolloClient.query(rawQuery).execute()

            if (response.exception != null) return Result.failure(response.exception!!)
            if (!response.errors.isNullOrEmpty()) return Result.failure(Exception(response.errors?.joinToString { it.message }))

            Result.success(response.data?.rawData.toString())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
