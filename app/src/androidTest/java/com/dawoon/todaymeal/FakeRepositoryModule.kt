package com.dawoon.todaymeal.di

import com.dawoon.todaymeal.network.ApiResult
import com.dawoon.todaymeal.network.model.MealRowDto
import com.dawoon.todaymeal.network.model.SchoolRowDto
import com.dawoon.todaymeal.network.model.SchoolScheduleRowDto
import com.dawoon.todaymeal.repository.MealRepository
import com.dawoon.todaymeal.repository.ScheduleRepository
import com.dawoon.todaymeal.repository.SettingRepository
import com.dawoon.todaymeal.repository.TimetableRepository
import com.dawoon.todaymeal.repository.TimetableSubject
import com.dawoon.todaymeal.util.PreferenceManager
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [
        RepositoryModule::class,
        PreferenceModule::class
    ]
)
object FakeRepositoryModule {

    @Provides
    @Singleton
    fun provideMealRepository(): MealRepository = mockk<MealRepository>().apply {
        coEvery {
            getMealServiceInfo(any(), any(), any(), any(), any())
        } returns ApiResult.Success(
            listOf(
                MealRowDto(
                    DDISH_NM = "제육볶음<br/>현미밥<br/>깍두기",
                    MLSV_YMD = "20250514",
                    CAL_INFO = "550 kcal",
                    NTR_INFO = "단백질/지방/탄수화물"
                )
            )
        )
    }

    @Provides
    @Singleton
    fun provideScheduleRepository(): ScheduleRepository = mockk<ScheduleRepository>(relaxed = true).apply {
        coEvery {
            getSchoolSchedule(any(), any(), "20260201", "20260228")
        } returns ApiResult.Success(
            listOf(
                SchoolScheduleRowDto(
                    AA_YMD = "20260225",
                    EVENT_NM = "겨울방학"
                ),
                SchoolScheduleRowDto(
                    AA_YMD = "20260228",
                    EVENT_NM = "졸업식"
                )
            )
        )

        coEvery {
            getSchoolSchedule(any(), any(), "20260101", "20260131")
        } returns ApiResult.Success(emptyList())

        coEvery {
            getSchoolSchedule(any(), any(), "20260301", "20260331")
        } returns ApiResult.Success(emptyList())

    }


    @Provides
    @Singleton
    fun provideSettingRepository(): SettingRepository = mockk<SettingRepository>(relaxed = true).apply {
        coEvery {
            searchSchool(any())
        } returns ApiResult.Success(
            listOf(
                SchoolRowDto(
                    SCHUL_NM = "테스트 고등학교",
                    SD_SCHUL_CODE = "7010000",
                    ATPT_OFCDC_SC_CODE = "B10",
                    ORG_RDNMA = "서울특별시"
                ),
                SchoolRowDto(
                    SCHUL_NM = "데이터 중학교",
                    SD_SCHUL_CODE = "7020000",
                    ATPT_OFCDC_SC_CODE = "B10",
                    ORG_RDNMA = "경기도"
                )
            )
        )
    }


    @Provides
    @Singleton
    fun provideTimetableRepository(): TimetableRepository = mockk<TimetableRepository>(relaxed = true).apply {
        coEvery {
            getTimetable(any(), any(), any(), any(), any(), any(), any())
        } returns ApiResult.Success(
            listOf(
                // 월요일(1) 1교시(1) 수학
                TimetableSubject(dayOfWeek = 1, period = 1, subject = "수학"),
                // 화요일(2) 2교시(2) 영어
                TimetableSubject(dayOfWeek = 2, period = 2, subject = "영어"),
                // 수요일(3) 3교시(3) 과학
                TimetableSubject(dayOfWeek = 3, period = 3, subject = "과학")
            )
        )
    }

    @Provides
    @Singleton
    fun providePreferenceManager(): PreferenceManager {
        return mockk<PreferenceManager>(relaxed = true)
    }
}