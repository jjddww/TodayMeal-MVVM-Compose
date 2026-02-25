package com.dawoon.todaymeal.screen

import com.dawoon.todaymeal.ui.screen.TimetableScreen
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.dawoon.todaymeal.HiltTestActivity
import com.dawoon.todaymeal.util.PreferenceManager
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.every
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject


@HiltAndroidTest
class TimetableScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<HiltTestActivity>()

    @Inject
    lateinit var preferenceManager: PreferenceManager

    @Before
    fun init() {
        hiltRule.inject()
        every { preferenceManager.schoolNameFlow } returns MutableStateFlow("테스트 고등학교")
        coEvery { preferenceManager.getSchoolCode() } returns "7010000"
        coEvery { preferenceManager.getAtptCode() } returns "B10"
        coEvery { preferenceManager.getSchoolName() } returns "테스트 고등학교"

        coEvery { preferenceManager.getSchoolType() } returns "HIGH"
        coEvery { preferenceManager.getGrade() } returns "2"
        coEvery { preferenceManager.getClass() } returns "6"
    }

    @Test
    fun timetable_ShouldDisplaySubjects_WhenDataIsLoaded() {
        composeTestRule.setContent {
            TimetableScreen()
        }

        composeTestRule.waitUntil(5000) {
            composeTestRule.onAllNodesWithText("수학").fetchSemanticsNodes().isNotEmpty()
        }

        // 월요일 1교시 수학
        composeTestRule.onNodeWithText("수학").assertIsDisplayed()

        // 화요일 2교시 영어
        composeTestRule.onNodeWithText("영어").assertIsDisplayed()

        // 수요일 3교시 과학
        composeTestRule.onNodeWithText("과학").assertIsDisplayed()

        composeTestRule.onAllNodesWithText("정보\n없음").onFirst().assertIsDisplayed()
    }

    @Test
    fun timetable_Header_ShouldBeVisible() {
        composeTestRule.setContent {
            TimetableScreen()
        }

        composeTestRule.onNodeWithText("시간표").assertIsDisplayed()
        composeTestRule.onNodeWithText("월").assertIsDisplayed()
        composeTestRule.onNodeWithText("금").assertIsDisplayed()
        composeTestRule.onNodeWithText("4").assertIsDisplayed() // 1~7교시 숫자 중 아무거나
    }

    @After
    fun tearDown() {
        clearMocks(preferenceManager, answers = true)
    }
}