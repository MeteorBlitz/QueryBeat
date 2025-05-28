package com.example.querybeat.data.repository.country

import com.example.querybeat.GetCountriesQuery
import com.example.querybeat.data.model.Country
import com.example.querybeat.data.remote.GraphQLClient
import jakarta.inject.Inject

class CountryRepositoryImpl @Inject constructor() : CountryRepository {
    override suspend fun getCountries(): List<Country> {
        val response = GraphQLClient.apolloClient.query(GetCountriesQuery()).execute()
        return response.data?.countries?.map {
            Country(it.code, it.name, it.emoji)
        } ?: emptyList()
    }
}
