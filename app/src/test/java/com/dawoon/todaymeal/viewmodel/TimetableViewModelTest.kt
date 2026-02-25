package com.dawoon.todaymeal.viewmodel

import app.cash.turbine.test
import com.dawoon.todaymeal.network.ApiResult
import com.dawoon.todaymeal.repository.TimetableRepository
import com.dawoon.todaymeal.repository.TimetableSubject
import com.dawoon.todaymeal.util.PreferenceManager
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TimetableViewModelTest {

    private lateinit var viewModel: TimetableViewModel
    private val repository = mockk<TimetableRepository>()
    private val prefManager = mockk<PreferenceManager>()

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        coEvery { prefManager.getSchoolType() } returns "HIGH"
        coEvery { prefManager.getAtptCode() } returns "T10"
        coEvery { prefManager.getSchoolCode() } returns "12345"
        coEvery { prefManager.getGrade() } returns "3"
        coEvery { prefManager.getClass() } returns "1"
    }

    @Test
    fun `fetchTimetable success should update timetableState and uiState`() = runTest {
        val mockData = listOf(
            TimetableSubject(dayOfWeek = 1, period = 1, subject = "수학"),
            TimetableSubject(dayOfWeek = 1, period = 2, subject = "영어")
        )
        coEvery {
            repository.getTimetable(any(), any(), any(), any(), any(), any(), any())
        } returns ApiResult.Success(mockData)

        viewModel = TimetableViewModel(repository, prefManager)
        viewModel.fetchTimetable("20260225", "20260225")

        advanceUntilIdle()

        assertEquals(mockData, viewModel.timetableState.value)
        assertEquals(false, viewModel.uiState.value.isLoading)
        assertEquals(null, viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `fetchTimetable INFO-200 should update infoMessage and clear list`() = runTest {
        coEvery {
            repository.getTimetable(any(), any(), any(), any(), any(), any(), any())
        } returns ApiResult.Error("INFO-200", "정보 없음")

        viewModel = TimetableViewModel(repository, prefManager)
        viewModel.fetchTimetable("20260225", "20260225")

        advanceUntilIdle()

        assertEquals(true, viewModel.timetableState.value.isEmpty())
        assertEquals("해당 기간에 등록된 시간표가 없습니다.", viewModel.uiState.value.infoMessage)
        assertEquals(null, viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `fetchTimetable failure should update errorMessage`() = runTest {
        coEvery {
            repository.getTimetable(any(), any(), any(), any(), any(), any(), any())
        } returns ApiResult.Failure(Exception("Network Down"))

        viewModel = TimetableViewModel(repository, prefManager)
        viewModel.fetchTimetable("20260225", "20260225")

        advanceUntilIdle()


        assertEquals("네트워크 오류가 발생했습니다.\n잠시 후 다시 시도해주세요.", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `maxPeriod should be at least 7 even if data is empty`() = runTest {
        coEvery {
            repository.getTimetable(any(), any(), any(), any(), any(), any(), any())
        } returns ApiResult.Success(emptyList())

        viewModel = TimetableViewModel(repository, prefManager)

        viewModel.maxPeriod.test {
            assertEquals(7, awaitItem())
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}