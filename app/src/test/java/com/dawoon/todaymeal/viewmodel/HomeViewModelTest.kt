package com.dawoon.todaymeal.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.dawoon.todaymeal.network.ApiResult
import com.dawoon.todaymeal.network.model.MealRowDto
import com.dawoon.todaymeal.repository.MealRepository
import com.dawoon.todaymeal.util.DateCalculator
import com.dawoon.todaymeal.util.PreferenceManager
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@get:Rule
val instantTaskExecutorRule = InstantTaskExecutorRule()

@OptIn(ExperimentalCoroutinesApi::class)

class HomeViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    private val repository = mockk<MealRepository>()
    private val prefManager = mockk<PreferenceManager>()

    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        coEvery { prefManager.schoolNameFlow } returns flowOf("테스트고등학교")
        coEvery { prefManager.getAtptCode() } returns "T10"
        coEvery { prefManager.getSchoolCode() } returns "12345"

        coEvery {
            repository.getMealServiceInfo(any(), any(), any(), any(), any())
        } returns ApiResult.Success(emptyList())
    }

    @Test
    fun `toggle nutrition dialog should update showNutritionDialog state`() {
        viewModel = HomeViewModel(repository, prefManager)

        viewModel.run { openNutritionDialog() }

        assertEquals(true, viewModel.showNutritionDialog)

        viewModel.closeNutritionDialog()

        assertEquals(false, viewModel.showNutritionDialog)
    }

    @Test
    fun `updateSelectedDate with valid string should update selectedDate`() {
        viewModel = HomeViewModel(repository, prefManager)
        val dateStr = "20260225"
        val expectedDate = DateCalculator.parseApiDate(dateStr)

        viewModel.updateSelectedDate(dateStr)

        assertEquals(expectedDate, viewModel.selectedDate)
    }

    @Test
    fun `updateMealType with different type should change selectedMealType and re-fetch data`() = runTest {
        viewModel = HomeViewModel(repository, prefManager)
        val newType = "1" // 아침

        coEvery {
            repository.getMealServiceInfo(any(), any(), newType, any(), any())
        } returns ApiResult.Success(emptyList())

        viewModel.updateMealType(newType)
        advanceUntilIdle()

        assertEquals(newType, viewModel.selectedMealType)
        coVerify {
            repository.getMealServiceInfo(any(), any(), newType, any(), any())
        }
    }

    @Test
    fun `resetSetting should call prefManager clearAll`() = runTest {
        viewModel = HomeViewModel(repository, prefManager)
        coEvery { prefManager.clearAll() } just Runs

        viewModel.resetSetting()

        coVerify { prefManager.clearAll() }
    }


    @Test
    fun `load meal data successfully should update state to success`() = runTest {
        val today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))

        val mockApiList = listOf(
            MealRowDto(
                MLSV_YMD = today,
                DDISH_NM = "테스트용 카레라이스"
            )
        )

        coEvery {
            repository.getMealServiceInfo(any(), any(), any(), any(), any())
        } returns ApiResult.Success(mockApiList)

        viewModel = HomeViewModel(repository, prefManager)
        advanceUntilIdle()

        viewModel.state.test {
            val actualState = expectMostRecentItem()
            val meal = actualState.items.find { it.MLSV_YMD == today }

            assertEquals("테스트용 카레라이스", meal?.DDISH_NM)
        }
    }

    @Test
    fun `fetch meal data failure should show error message and error data`() = runTest {
        coEvery {
            repository.getMealServiceInfo(any(), any(), any(), any(), any())
        } returns ApiResult.Failure(Exception("연결 실패"))

        viewModel = HomeViewModel(repository, prefManager)
        advanceUntilIdle()

        viewModel.state.test { //then
            val actualState = awaitItem()
            assertEquals("연결 실패", actualState.errorMessage)
            assertEquals("문제가 \n발생하였습니다.\n잠시 후 \n다시 시도해주세요", actualState.items[0].MMEAL_SC_NM)
        }
    }


    @Test
    fun `api error with specific code should update state with error message`() = runTest {
        coEvery {
            repository.getMealServiceInfo(any(), any(), any(), any(), any())
        } returns ApiResult.Error("INFO-200", "No data available")

        // When
        viewModel = HomeViewModel(repository, prefManager)
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val actualState = awaitItem()
            assert(actualState.errorMessage?.contains("INFO-200") == true)
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}