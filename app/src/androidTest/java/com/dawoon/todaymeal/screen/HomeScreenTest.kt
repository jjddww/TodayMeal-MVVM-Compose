package com.dawoon.todaymeal.screen

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.compose.rememberNavController
import com.dawoon.todaymeal.MainActivity
import com.dawoon.todaymeal.ui.screen.HomeScreen
import com.dawoon.todaymeal.util.PreferenceManager
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.every
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import javax.inject.Inject

@HiltAndroidTest
class HomeScreenTest {
    private val hiltRule = HiltAndroidRule(this)

    val composeTestRule = createAndroidComposeRule<com.dawoon.todaymeal.HiltTestActivity>()

    @get:Rule
    val chain: RuleChain = RuleChain.outerRule(hiltRule).around(composeTestRule)

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
    fun should_DisplaySchoolName_When_ScreenIsLoaded() { //학교 타이틀 잘 보이는지
        composeTestRule.setContent {
            val navController = rememberNavController()
            HomeScreen(navController = navController)
        }
        composeTestRule.onNodeWithText("테스트 고등학교").assertIsDisplayed()
    }

    @Test
    fun should_DisplayMeal_When_ScreenIsLoaded() { //식단내용이 잘 보이는지
        composeTestRule.setContent {
            val navController = rememberNavController()
            HomeScreen(navController = navController)
        }

        composeTestRule.onNodeWithText("제육볶음", substring = true).assertIsDisplayed()
    }

    @Test
    fun should_OpenNavigationDrawer_When_MenuIconIsClicked() { //메뉴 아이콘 눌렀을때
        composeTestRule.setContent {
            val navController = rememberNavController()
            HomeScreen(navController = navController)
        }

        composeTestRule.onNodeWithContentDescription("Menu").performClick()

        composeTestRule.onNodeWithText("설정 초기화").assertIsDisplayed()
    }

    @Test
    fun should_ShowNutritionDialog_When_NutritionButtonIsClicked() { //영양정보 버튼 누르고 팝업 띄웠을때
        composeTestRule.setContent {
            val navController = rememberNavController()
            HomeScreen(navController = navController)
        }

        composeTestRule.onNodeWithText("영양정보").performClick()
        composeTestRule.onNodeWithText("550", substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("단백질/지방/탄수화물").assertIsDisplayed()
        composeTestRule.onNodeWithText("닫기").assertIsDisplayed()
    }

    @Test
    fun should_ChangeDate_When_LeftArrowIconIsClicked() { //왼쪽 날짜 이동 눌렀을때
        composeTestRule.setContent {
            val navController = rememberNavController()
            HomeScreen(navController = navController)
        }

        composeTestRule.onNodeWithText("14", substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("left arrow").performClick()
        composeTestRule.onNodeWithText("14", substring = true).assertDoesNotExist()
        composeTestRule.onNodeWithText("13", substring = true).assertIsDisplayed()
    }

    @Test
    fun should_ChangeDate_When_RightArrowIconIsClicked() { //오른쪽 날짜 이동 눌렀을때
        composeTestRule.setContent {
            val navController = rememberNavController()
            HomeScreen(navController = navController)
        }

        composeTestRule.onNodeWithText("14", substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("right arrow").performClick()
        composeTestRule.onNodeWithText("14", substring = true).assertDoesNotExist()
        composeTestRule.onNodeWithText("15", substring = true).assertIsDisplayed()
    }

    @Test
    fun should_ShowResetDialog_When_ResetMenuIsClickedInDrawer() { //설정 초기화
        composeTestRule.setContent {
            val navController = rememberNavController()
            HomeScreen(navController = navController)
        }

        composeTestRule.onNodeWithContentDescription("Menu").performClick()
        composeTestRule.onNodeWithText("설정 초기화").performClick()
        composeTestRule.onNodeWithText("정말 초기화하시겠습니까?", substring = true).assertIsDisplayed()
    }

    @Test
    fun should_ShowResetDialog_When_EmailIsClickedInDrawer() { //문의사항
        composeTestRule.setContent {
            val navController = rememberNavController()
            HomeScreen(navController = navController)
        }

        composeTestRule.onNodeWithContentDescription("Menu").performClick()
        composeTestRule.onNodeWithText("문의 사항").performClick()
        composeTestRule.onNodeWithText("메일로 의견을 남겨주세요!", substring = true).assertIsDisplayed()
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }
}