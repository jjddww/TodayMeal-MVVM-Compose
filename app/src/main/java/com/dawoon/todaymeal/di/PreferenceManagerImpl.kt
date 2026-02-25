package com.dawoon.todaymeal.di

import com.dawoon.todaymeal.util.PreferenceManager
import com.dawoon.todaymeal.util.PreferenceManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PreferenceModule {
    @Binds
    @Singleton
    abstract fun bindPreferenceManager(
        preferenceManagerImpl: PreferenceManagerImpl
    ): PreferenceManager
}