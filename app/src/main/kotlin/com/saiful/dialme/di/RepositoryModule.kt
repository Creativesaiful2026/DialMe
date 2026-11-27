package com.saiful.dialme.di

import android.content.Context
import com.saiful.dialme.core.data.calllog.CallLogRepository
import com.saiful.dialme.core.data.contacts.ContactsRepository
import com.saiful.dialme.core.data.theme.DataStoreThemeRepository
import com.saiful.dialme.core.model.theme.ThemeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideThemeRepository(@ApplicationContext context: Context): ThemeRepository =
        DataStoreThemeRepository(context)

    @Provides
    @Singleton
    fun provideContactsRepository(@ApplicationContext context: Context): ContactsRepository =
        ContactsRepository(context)

    @Provides
    @Singleton
    fun provideCallLogRepository(@ApplicationContext context: Context): CallLogRepository =
        CallLogRepository(context)
}
