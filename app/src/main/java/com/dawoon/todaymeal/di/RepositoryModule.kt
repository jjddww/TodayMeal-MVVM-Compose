package com.dawoon.todaymeal.di

import com.dawoon.todaymeal.repository.MealRepository
import com.dawoon.todaymeal.repository.MealRepositoryImpl
import com.dawoon.todaymeal.repository.ScheduleRepository
import com.dawoon.todaymeal.repository.ScheduleRepositoryImpl
import com.dawoon.todaymeal.repository.SettingRepository
import com.dawoon.todaymeal.repository.SettingRepositoryImpl
import com.dawoon.todaymeal.repository.TimetableRepository
import com.dawoon.todaymeal.repository.TimetableRepositoryImpl
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
    abstract fun bindMealRepository(
        mealRepositoryImpl: MealRepositoryImpl
    ): MealRepository

    @Binds
    @Singleton
    abstract fun bindScheduleRepository(
        scheduleRepositoryImpl: ScheduleRepositoryImpl
    ): ScheduleRepository

    @Binds
    @Singleton
    abstract fun bindSettingRepository(
        settingRepositoryImpl: SettingRepositoryImpl
    ): SettingRepository

    @Binds
    @Singleton
    abstract fun bindTimetableRepository(
        timetableRepositoryImpl: TimetableRepositoryImpl
    ): TimetableRepository
}