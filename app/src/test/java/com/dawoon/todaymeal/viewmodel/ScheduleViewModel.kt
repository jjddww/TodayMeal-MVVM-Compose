package com.dawoon.todaymeal.viewmodel

import app.cash.turbine.test
import com.dawoon.todaymeal.network.ApiResult
import com.dawoon.todaymeal.network.model.SchoolScheduleRowDto
import com.dawoon.todaymeal.repository.ScheduleRepository
import com.dawoon.todaymeal.util.PreferenceManager
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ScheduleViewModelTest {
    private lateinit var viewModel: ScheduleViewModel
    private val repository = mockk<ScheduleRepository>()
    private val prefManager = mockk<PreferenceManager>()

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        coEvery { prefManager.getAtptCode() } returns "T10"
        coEvery { prefManager.getSchoolCode() } returns "12345"

        coEvery {
            repository.getSchoolSchedule(any(), any(), any(), any())
        } returns ApiResult.Success(emptyList())
    }


    @Test
    fun `fetchScheduleData success should update items and loading false`() = runTest {
        val mockSchedules = listOf(
            SchoolScheduleRowDto(EVENT_NM = "개학식"),
            SchoolScheduleRowDto(EVENT_NM = "토요휴업일")
        )
        coEvery {
            repository.getSchoolSchedule(any(), any(), any(), any())
        } returns ApiResult.Success(mockSchedules)

        viewModel = ScheduleViewModel(repository, prefManager)
        advanceUntilIdle()

        viewModel.state.test {
            val actualState = awaitItem()
            assertEquals(mockSchedules, actualState.items)
            assertEquals(false, actualState.loading)
        }
    }

    @Test
    fun `moveToNextMonth should trigger new fetch with next month range`() = runTest {
        viewModel = ScheduleViewModel(repository, prefManager)
        val initialMonth = viewModel.displayMonth // 현재 월 확인

        coEvery {
            repository.getSchoolSchedule(any(), any(), any(), any())
        } returns ApiResult.Success(listOf(SchoolScheduleRowDto(EVENT_NM = "중간고사")))

        viewModel.moveToNextMonth()
        advanceUntilIdle()

        val nextMonth = viewModel.displayMonth
        assert(initialMonth != nextMonth)

        assertEquals("중간고사", viewModel.state.value.items.first().EVENT_NM)
    }

    @Test
    fun `onError should update items with errorData and show errorMessage`() = runTest {
        coEvery {
            repository.getSchoolSchedule(any(), any(), any(), any())
        } returns ApiResult.Error("ERROR-300", "Invalid Key")

        viewModel = ScheduleViewModel(repository, prefManager)
        advanceUntilIdle()

        val actualState = viewModel.state.value
        assertEquals("문제가 \n발생하였습니다.\n잠시 후 \n다시 시도해주세요", actualState.items.first().EVENT_NM)
        assert(actualState.errorMessage?.contains("API ERROR ERROR-300") == true)
    }

    @Test
    fun `onFailure should update errorMessage with exception message`() = runTest {
        val exceptionMsg = "Unknown Host Exception"
        coEvery {
            repository.getSchoolSchedule(any(), any(), any(), any())
        } returns ApiResult.Failure(Exception(exceptionMsg))

        viewModel = ScheduleViewModel(repository, prefManager)
        advanceUntilIdle()

        assertEquals(exceptionMsg, viewModel.state.value.errorMessage)
    }


    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}