package com.dawoon.todaymeal.viewmodel

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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

@OptIn(ExperimentalCoroutinesApi::class)
class WeeklyMenuViewModelTest {

    private lateinit var viewModel: WeeklyMenuViewModel
    private val repository = mockk<MealRepository>()
    private val prefManager = mockk<PreferenceManager>()
    private val testDispatcher = StandardTestDispatcher()

    private val today = LocalDate.now()
    private val todayStr = today.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
    private val currentMonday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    private val currentMondayStr = currentMonday.format(DateTimeFormatter.ofPattern("yyyyMMdd"))

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
    fun `init should generate weeks for current month and set initial index`() = runTest {
        viewModel = WeeklyMenuViewModel(repository, prefManager)

        assertTrue(viewModel.weeks.value.isNotEmpty())

        val currentIndex = viewModel.selectedWeekIndex.value
        val currentWeek = viewModel.weeks.value[currentIndex]

        assertTrue(todayStr >= currentWeek.startDate && todayStr <= currentWeek.endDate)
    }

    @Test
    fun `generateWeeks logic should create correct week ranges`() = runTest {
        viewModel = WeeklyMenuViewModel(repository, prefManager)
        val generatedWeeks = viewModel.weeks.value

        assertTrue(generatedWeeks.size >= 4)

        val week1 = generatedWeeks[0]
        assertEquals("1주차", week1.weekLabel)
        assertTrue(week1.startDate.length == 8)
        assertTrue(week1.endDate.length == 8)

        val lastWeek = generatedWeeks.last()
        assertEquals("${generatedWeeks.size}주차", lastWeek.weekLabel)
    }

    @Test
    fun `initial week index should be correctly set to current week`() = runTest {
        viewModel = WeeklyMenuViewModel(repository, prefManager)

        val currentIndex = viewModel.selectedWeekIndex.value
        val selectedWeek = viewModel.weeks.value[currentIndex]

        assertEquals(currentMondayStr, selectedWeek.startDate)
    }

    @Test
    fun `loadWeeklyData success should fill empty dates with placeholders`() = runTest {
        val mockApiData = listOf(
            MealRowDto(MLSV_YMD = todayStr, DDISH_NM = "김치볶음밥", MMEAL_SC_NM = "점심")
        )

        coEvery {
            repository.getMealServiceInfo(any(), any(), any(), any(), any())
        } returns ApiResult.Success(mockApiData)

        viewModel = WeeklyMenuViewModel(repository, prefManager)
        advanceUntilIdle()

        val items = viewModel.uiState.value.items
        assertEquals(7, items.size)

        val dayToday = items.find { it.MLSV_YMD == todayStr }
        assertEquals("김치볶음밥", dayToday?.DDISH_NM)

        val tomorrow = today.plusDays(1)
        if (tomorrow.isBefore(currentMonday.plusDays(7))) {
            val tomorrowStr = tomorrow.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
            val dayTomorrow = items.find { it.MLSV_YMD == tomorrowStr }
            assertEquals("급식 정보가 없습니다.", dayTomorrow?.DDISH_NM)
        }
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
        advanceUntilIdle()

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
        advanceUntilIdle()

        coVerify(exactly = 1) { repository.getMealServiceInfo(any(), any(), any(), any(), any()) }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}