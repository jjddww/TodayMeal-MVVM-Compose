package com.dawoon.todaymeal.viewmodel

import app.cash.turbine.test
import com.dawoon.todaymeal.network.ApiResult
import com.dawoon.todaymeal.network.model.MealRowDto
import com.dawoon.todaymeal.repository.MealRepository
import com.dawoon.todaymeal.util.PreferenceManager
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WeeklyMenuViewModelTest {

    private lateinit var viewModel: WeeklyMenuViewModel
    private val repository = mockk<MealRepository>()
    private val prefManager = mockk<PreferenceManager>()

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        coEvery { prefManager.getAtptCode() } returns "T10"
        coEvery { prefManager.getSchoolCode() } returns "12345"

        coEvery {
            repository.getMealServiceInfo(any(), any(), any(), any(), any())
        } returns ApiResult.Success(emptyList())
    }


    @Test
    fun `init should generate weeks for May 2025 and set initial index`() = runTest {
        viewModel = WeeklyMenuViewModel(repository, prefManager)

        assert(viewModel.weeks.value.isNotEmpty())

        val currentIndex = viewModel.selectedWeekIndex.value
        val currentWeek = viewModel.weeks.value[currentIndex]
        assert("20250514" >= currentWeek.startDate && "20250514" <= currentWeek.endDate)
    }


    @Test
    fun `generateWeeks logic should create correct May 2025 week ranges`() = runTest {
        viewModel = WeeklyMenuViewModel(repository, prefManager)

        val generatedWeeks = viewModel.weeks.value

        assert(generatedWeeks.size >= 4)

        val week1 = generatedWeeks[0]
        assertEquals("1주차", week1.weekLabel)
        assertEquals("20250428", week1.startDate)
        assertEquals("20250504", week1.endDate)

        val week3 = generatedWeeks[2]
        assertEquals("3주차", week3.weekLabel)
        assertEquals("20250512", week3.startDate)
        assertEquals("20250518", week3.endDate)


        val lastWeek = generatedWeeks.last()
        val totalWeeks = generatedWeeks.size

        assertEquals("${totalWeeks}주차", lastWeek.weekLabel)
        assertEquals("20250526", lastWeek.startDate)
        assertEquals("20250601", lastWeek.endDate)
    }

    @Test
    fun `initial week index should be correctly set to index 2 for May 14th`() = runTest {
        viewModel = WeeklyMenuViewModel(repository, prefManager)
        assertEquals(2, viewModel.selectedWeekIndex.value)
    }

    @Test
    fun `loadWeeklyData success should fill empty dates with placeholders`() = runTest {
        val mockApiData = listOf(
            MealRowDto(MLSV_YMD = "20250514", DDISH_NM = "김치볶음밥", MMEAL_SC_NM = "점심")
        )
        coEvery {
            repository.getMealServiceInfo(any(), any(), "2", any(), any())
        } returns ApiResult.Success(mockApiData)

        viewModel = WeeklyMenuViewModel(repository, prefManager)
        advanceUntilIdle()

        val items = viewModel.uiState.value.items
        assertEquals(7, items.size)

        val day14 = items.find { it.MLSV_YMD == "20250514" }
        assertEquals("김치볶음밥", day14?.DDISH_NM)

        val day15 = items.find { it.MLSV_YMD == "20250515" }
        assertEquals("급식 정보가 없습니다.", day15?.DDISH_NM)
    }

    @Test
    fun `loadWeeklyData failure should update errorMessage`() = runTest {
        val errorMsg = "Network Error"
        coEvery {
            repository.getMealServiceInfo(any(), any(), any(), any(), any())
        } returns ApiResult.Failure(Exception(errorMsg))

        viewModel = WeeklyMenuViewModel(repository, prefManager)
        advanceUntilIdle()

        assertEquals(errorMsg, viewModel.uiState.value.errorMessage)
        assertEquals(false, viewModel.uiState.value.loading)
    }

    @Test
    fun `updateMealType should trigger new data load and change meal name`() = runTest {
        viewModel = WeeklyMenuViewModel(repository, prefManager)

        coEvery {
            repository.getMealServiceInfo(any(), any(), "3", any(), any())
        } returns ApiResult.Success(emptyList())

        viewModel.updateMealType("3")
        advanceUntilIdle()

        assertEquals("3", viewModel.selectedMealType)
        assertEquals("저녁", viewModel.uiState.value.items.first().MMEAL_SC_NM)
    }



    @Test
    fun `updateWeekIndex with same index should not trigger reload`() = runTest {
        viewModel = WeeklyMenuViewModel(repository, prefManager)
        advanceUntilIdle()

        coVerify(exactly = 1) { repository.getMealServiceInfo(any(), any(), any(), any(), any()) }

        viewModel.updateWeekIndex(viewModel.selectedWeekIndex.value)

        coVerify(exactly = 1) { repository.getMealServiceInfo(any(), any(), any(), any(), any()) }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

}