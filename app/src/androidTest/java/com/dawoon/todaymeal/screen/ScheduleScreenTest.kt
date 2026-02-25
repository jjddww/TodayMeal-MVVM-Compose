package com.dawoon.todaymeal.screen

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.dawoon.todaymeal.ui.screen.ScheduleScreen
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@HiltAndroidTest
class ScheduleScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<com.dawoon.todaymeal.HiltTestActivity>()


    @Before
    fun init() {
        hiltRule.inject()
    }


    @Test
    fun should_DisplayTitle_When_ScreenIsLoaded() {
        composeTestRule.setContent {
            ScheduleScreen()
        }

        composeTestRule
            .onNodeWithText("학사일정")
            .assertIsDisplayed()
    }

    @Test
    fun should_ChangeMonth_When_LeftArrowIconsAreClicked() {
        composeTestRule.setContent {
            ScheduleScreen()
        }

        composeTestRule
            .onNodeWithContentDescription("left arrow")
            .performClick()

        composeTestRule
            .onNodeWithText("1월")
            .assertIsDisplayed()
    }

    @Test
    fun should_ChangeMonth_When_RightArrowIconsAreClicked() {
        composeTestRule.setContent {
            ScheduleScreen()
        }

        composeTestRule
            .onNodeWithContentDescription("right arrow")
            .performClick()

        composeTestRule
            .onNodeWithText("3월")
            .assertIsDisplayed()
    }

    @Test
    fun should_ShowEmptyMessage_When_NoScheduleExists() {
        composeTestRule.setContent {
            ScheduleScreen()
        }
        composeTestRule
            .onNodeWithContentDescription("right arrow")
            .performClick()

        composeTestRule
            .onNodeWithText("해당 월에 학사일정이 없습니다.")
            .assertIsDisplayed()
    }

    @Test
    fun should_DisplayScheduleList_When_DataExists() {
        composeTestRule.setContent {
            ScheduleScreen()
        }
         composeTestRule.onNodeWithText("졸업식").assertIsDisplayed()
    }
}