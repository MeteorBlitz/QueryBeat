package com.example.querybeat.di

import android.app.Application
import android.content.Context
import com.apollographql.apollo.ApolloClient
import com.example.querybeat.data.repository.Schema.SchemaRepository
import com.example.querybeat.data.repository.Schema.SchemaRepositoryImpl
import com.example.querybeat.data.repository.country.CountryRepository
import com.example.querybeat.data.repository.country.CountryRepositoryImpl
import com.example.querybeat.data.repository.results.ResultsRepository
import com.example.querybeat.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideApolloClient(): ApolloClient {
        return ApolloClient.Builder()
            .serverUrl(Constants.GRAPHQL_BASE_URL)
            .build()
    }

    @Provides
    @Singleton
    fun provideCountryRepository(apolloClient: ApolloClient): CountryRepository {
        return CountryRepositoryImpl(apolloClient)
    }

    @Provides
    @Singleton
    fun provideSchemaRepository(
        @ApplicationContext context: Context
    ): SchemaRepository {
        return SchemaRepositoryImpl(context as Application)
    }

    @Provides
    @Singleton
    fun provideResultsRepository(
        apolloClient: ApolloClient
    ): ResultsRepository {
        return ResultsRepository(apolloClient)
    }

}