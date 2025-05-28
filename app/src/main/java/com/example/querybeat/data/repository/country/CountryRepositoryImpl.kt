package com.example.querybeat.data.repository.country

import com.apollographql.apollo.ApolloClient
import com.example.querybeat.GetCountriesQuery
import com.example.querybeat.data.model.Country
import jakarta.inject.Inject

class CountryRepositoryImpl @Inject constructor(private val apolloClient: ApolloClient) : CountryRepository {
    override suspend fun getCountries(): List<Country> {
        val response = apolloClient.query(GetCountriesQuery()).execute()
        return response.data?.countries?.map {
            Country(it.code, it.name, it.emoji)
        } ?: emptyList()
    }
}
