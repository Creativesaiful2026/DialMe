package com.saiful.dialme.di

import com.saiful.dialme.core.model.theme.ThemeRepository
import com.saiful.dialme.data.InMemoryThemeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindThemeRepository(impl: InMemoryThemeRepository): ThemeRepository
}
