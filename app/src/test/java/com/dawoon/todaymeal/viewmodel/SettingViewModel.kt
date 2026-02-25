package com.dawoon.todaymeal.viewmodel

import com.dawoon.todaymeal.network.ApiResult
import com.dawoon.todaymeal.network.model.SchoolRowDto
import com.dawoon.todaymeal.repository.SettingRepository
import com.dawoon.todaymeal.util.PreferenceManager
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingViewModelTest {

    private lateinit var viewModel: SettingViewModel
    private val repository = mockk<SettingRepository>()
    private val prefManager = mockk<PreferenceManager>(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = SettingViewModel(repository, prefManager)
    }


    @Test
    fun `searchSchool success should update schoolList and showPopup`() = runTest {
        val mockSchools = listOf(SchoolRowDto(SCHUL_NM = "테스트중학교", SD_SCHUL_CODE = "12345"))
        viewModel.searchQuery = "테스트"
        coEvery { repository.searchSchool("테스트") } returns ApiResult.Success(mockSchools)

        viewModel.searchSchool()
        advanceUntilIdle()

        assertEquals(mockSchools, viewModel.schoolList.value)
        assertEquals(true, viewModel.showPopup)
        assertEquals("", viewModel.errorMessage)
        assertEquals(false, viewModel.isLoading)
    }

    @Test
    fun `searchSchool Error should set server error message`() = runTest {
        viewModel.searchQuery = "서울"
        coEvery { repository.searchSchool("서울") } returns ApiResult.Error("404", "Not Found")

        viewModel.searchSchool()
        advanceUntilIdle()

        assertEquals("서버 오류가 발생했습니다. (코드: 404)", viewModel.errorMessage)
        assertEquals(false, viewModel.showPopup)
        assertEquals(false, viewModel.isLoading)
    }

    @Test
    fun `searchSchool Failure should set network error message`() = runTest {
        viewModel.searchQuery = "서울"
        coEvery { repository.searchSchool("서울") } returns ApiResult.Failure(Exception("No Internet"))

        viewModel.searchSchool()
        advanceUntilIdle()

        assertEquals("네트워크 연결을 확인해주세요.", viewModel.errorMessage)
        assertEquals(false, viewModel.isLoading)
    }

    @Test
    fun `onSchoolSelected should hide schoolPopup and show gradePopup`() = runTest {
        val selectedSchool = SchoolRowDto(SCHUL_NM = "Test School", SD_SCHUL_CODE = "12345")

        viewModel.onSchoolSelected(selectedSchool)

        assertEquals(false, viewModel.showPopup)
        assertEquals(true, viewModel.showGradePopup)
    }

    @Test
    fun `saveFinalSettings success should save data and trigger onComplete`() = runTest {
        val selectedSchool = SchoolRowDto(
            SCHUL_NM = "테스트중학교",
            SD_SCHUL_CODE = "12345",
            ATPT_OFCDC_SC_CODE = "T10"
        )
        viewModel.onSchoolSelected(selectedSchool)
        viewModel.inputGrade = "1"
        viewModel.inputClass = "2"

        var isCompleted = false
        val onComplete = { isCompleted = true }

        viewModel.saveFinalSettings(onComplete)
        advanceUntilIdle()

        coVerify {
            prefManager.saveSchool("T10", "12345", "테스트중학교")
            prefManager.saveGradeAndClass("1", "2")
        }
        assertEquals(true, isCompleted)
    }

    @Test
    fun `saveFinalSettings should return early when tempSchool is null`() = runTest {
        val onComplete = mockk<() -> Unit>(relaxed = true)

        viewModel.saveFinalSettings(onComplete)
        advanceUntilIdle()

        coVerify(exactly = 0) { prefManager.saveSchool(any(), any(), any()) }
        verify(exactly = 0) { onComplete.invoke() }
    }

    @Test
    fun `saveFinalSettings should not save and not trigger onComplete when input is blank`() = runTest {
        val selectedSchool = SchoolRowDto(SCHUL_NM = "Test School", SD_SCHUL_CODE = "12345")
        viewModel.onSchoolSelected(selectedSchool)

        viewModel.inputGrade = ""
        viewModel.inputClass = ""

        val onComplete = mockk<() -> Unit>(relaxed = true)
        
        viewModel.saveFinalSettings(onComplete)
        advanceUntilIdle()

        coVerify(exactly = 0) { prefManager.saveSchool(any(), any(), any()) }
        coVerify(exactly = 0) { prefManager.saveGradeAndClass(any(), any()) }

        verify(exactly = 0) { onComplete.invoke() }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}