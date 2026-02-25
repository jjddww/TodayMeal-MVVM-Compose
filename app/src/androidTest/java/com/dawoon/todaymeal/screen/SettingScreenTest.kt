package com.dawoon.todaymeal.ui.screen

import android.content.Context
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.datastore.preferences.core.edit
import androidx.test.platform.app.InstrumentationRegistry
import com.dawoon.todaymeal.HiltTestActivity
import com.dawoon.todaymeal.util.PreferenceManager
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.every
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class SettingScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<HiltTestActivity>()

    @Inject
    lateinit var preferenceManager: PreferenceManager

    @Before
    fun init() {
        hiltRule.inject()

        every { preferenceManager.schoolNameFlow } returns MutableStateFlow("")
        coEvery { preferenceManager.getSchoolCode() } returns ""
        coEvery { preferenceManager.getAtptCode() } returns ""
        coEvery { preferenceManager.getSchoolName() } returns ""
        coEvery { preferenceManager.getSchoolType() } returns ""
        coEvery { preferenceManager.getGrade() } returns ""
        coEvery { preferenceManager.getClass() } returns ""

        coEvery { preferenceManager.saveSchool(any(), any(), any()) } returns Unit
        coEvery { preferenceManager.saveGradeAndClass(any(), any()) } returns Unit
    }

    @Test
    fun complete_setting_flow_successfully() {
        var navigated = false

        composeTestRule.setContent {
            SettingScreen(onNavigateToNext = { navigated = true })
        }

        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithText("학교 이름을 입력하세요")
            .performTextInput("테스트 고등학교")

        composeTestRule.onNodeWithText("확인").performClick()

        composeTestRule.waitUntil(5000) {
            composeTestRule.onAllNodesWithText("테스트 고등학교", substring = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule
            .onAllNodes(hasText("테스트 고등학교", substring = true) and hasClickAction())
            .onLast()
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("예: 3").performTextInput("3")
        composeTestRule.onNodeWithText("예: 1").performTextInput("1")

        composeTestRule.onNodeWithText("설정 완료").performClick()

        composeTestRule.waitUntil(5000) {
            navigated
        }

        assert(navigated)
    }

    @After
    fun tearDown() {
        clearMocks(preferenceManager)
    }
}