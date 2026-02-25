package com.dawoon.todaymeal.screen

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.dawoon.todaymeal.HiltTestActivity
import com.dawoon.todaymeal.ui.screen.WeeklyMenuScreen
import com.dawoon.todaymeal.util.PreferenceManager
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.coEvery
import io.mockk.every
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class WeeklyMenuScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<HiltTestActivity>()

    @Inject
    lateinit var preferenceManager: PreferenceManager

    @Before
    fun setup() {
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
    fun should_Display_Week_Number() {
        composeTestRule.setContent {
            WeeklyMenuScreen()
        }
        composeTestRule.onNodeWithText("1주차").assertIsDisplayed()
        composeTestRule.onNodeWithText("3주차").assertIsDisplayed()
    }

    @Test
    fun should_DisplayTitle_And_MealItems() {
        composeTestRule.setContent {
            WeeklyMenuScreen()
        }

        composeTestRule.onNodeWithText("주간 메뉴").assertIsDisplayed()

        composeTestRule.waitUntil(5000) {
            composeTestRule.onAllNodesWithText("제육볶음", substring = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText("제육볶음", substring = true).assertIsDisplayed()
    }

    @Test
    fun should_ChangeTabs_When_Clicked() {
        composeTestRule.setContent {
            WeeklyMenuScreen()
        }
        composeTestRule.onNodeWithText("저녁").performClick()
    }
}