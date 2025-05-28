package com.example.querybeat.di

import android.app.Application
import android.content.Context
import com.example.querybeat.data.repository.Schema.SchemaRepository
import com.example.querybeat.data.repository.Schema.SchemaRepositoryImpl
import com.example.querybeat.data.repository.country.CountryRepository
import com.example.querybeat.data.repository.country.CountryRepositoryImpl
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
    fun provideCountryRepository(): CountryRepository = CountryRepositoryImpl()

    @Provides
    @Singleton
    fun provideSchemaRepository(
        @ApplicationContext context: Context
    ): SchemaRepository {
        return SchemaRepositoryImpl(context as Application)
    }
}