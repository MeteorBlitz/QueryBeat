package com.example.querybeat.data.repository.country

import com.example.querybeat.data.model.Country

interface CountryRepository {
    suspend fun getCountries(): List<Country>
}