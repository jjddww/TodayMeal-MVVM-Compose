package com.dawoon.todaymeal.di

import com.dawoon.todaymeal.repository.SchoolRepository
import com.dawoon.todaymeal.util.PreferenceManager
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WidgetEntryPoint {
    fun schoolRepository(): SchoolRepository
    fun prefManager(): PreferenceManager
}