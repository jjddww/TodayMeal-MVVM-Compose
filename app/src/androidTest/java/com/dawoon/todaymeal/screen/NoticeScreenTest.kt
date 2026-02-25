package com.dawoon.todaymeal.screen

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.xr.runtime.Config
import com.dawoon.todaymeal.ui.screen.NoticeScreen
import com.dawoon.todaymeal.util.dummyNotices
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.clearAllMocks
import org.junit.After
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class NoticeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun should_DisplayHeader_When_ScreenIsLoaded() {
        composeTestRule.setContent {
            NoticeScreen()
        }

        composeTestRule
            .onNodeWithText("공지사항")
            .assertIsDisplayed()
    }

    @Test
    fun should_DisplayNoticeList_When_ScreenIsLoaded() {
        composeTestRule.setContent {
            NoticeScreen()
        }

        val firstNoticeTitle = dummyNotices.first().title
        composeTestRule
            .onNodeWithText(firstNoticeTitle)
            .assertIsDisplayed()
    }

    @Test
    fun should_ShowDialog_When_NoticeItemIsClicked() {
        composeTestRule.setContent {
            NoticeScreen()
        }

        val firstNotice = dummyNotices.first()
        composeTestRule
            .onNodeWithText(firstNotice.title)
            .performClick()

        composeTestRule
            .onNodeWithText(firstNotice.contents)
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("확인")
            .assertIsDisplayed()
    }

    @Test
    fun should_CloseDialog_When_ConfirmButtonIsClicked() {
        composeTestRule.setContent {
            NoticeScreen()
        }
        composeTestRule.onNodeWithText(dummyNotices.first().title).performClick()

        composeTestRule
            .onNodeWithText("확인")
            .performClick()

        composeTestRule
            .onNodeWithText(dummyNotices.first().contents)
            .assertDoesNotExist()
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }
}