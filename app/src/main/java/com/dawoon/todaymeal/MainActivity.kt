package com.dawoon.todaymeal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.RippleConfiguration
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.dawoon.todaymeal.ui.nav.BottomAppBar
import com.dawoon.todaymeal.ui.nav.HOME_ROUTE
import com.dawoon.todaymeal.ui.nav.HomeFab
import com.dawoon.todaymeal.ui.screen.HomeScreen
import com.dawoon.todaymeal.ui.screen.NoticeScreen
import com.dawoon.todaymeal.ui.screen.ScheduleScreen
import com.dawoon.todaymeal.ui.screen.TimetableScreen
import com.dawoon.todaymeal.ui.screen.WeeklyMenuScreen
import com.dawoon.todaymeal.ui.theme.DarkBackground
import com.dawoon.todaymeal.ui.theme.LightBackground
import com.dawoon.todaymeal.ui.theme.TodayMeal_MVVM_ComposeTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var isReady = false
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 2. 스플래시 화면 유지 조건 설정
        splashScreen.setKeepOnScreenCondition {
            // 이 값이 false인 동안은 스플래시가 사라지지 않습니다.
            !isReady
        }

        // 3. 의도적으로 1~2초 정도 딜레이를 줍니다 (테스트용)
        lifecycleScope.launch {
            delay(1200) // 1.5초 대기
            isReady = true // 로딩 완료 선언 -> 스플래시 사라짐
        }


        setContent {
            TodayMeal_MVVM_ComposeTheme() {
                val navController = rememberNavController()

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val route = navBackStackEntry?.destination?.route

                val isDark = isSystemInDarkTheme()
                val barBg = if (isDark) DarkBackground else LightBackground
//                val scaffoldBg = if (route == "notice") barBg else Color.Transparent

                val noRippleConfiguration = RippleConfiguration(
                    color = Color.Transparent,
                    rippleAlpha = RippleAlpha(0f, 0f, 0f, 0f)
                )
                CompositionLocalProvider(LocalRippleConfiguration provides noRippleConfiguration) {
                    Scaffold(
                        modifier = Modifier
                            .fillMaxSize(),
                        containerColor = barBg,
                        bottomBar = {
                            BottomAppBar(
                                navController)
                        } ,
                        floatingActionButton = {
                            HomeFab(
                                navController = navController) },
                        floatingActionButtonPosition = FabPosition.Center
                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = HOME_ROUTE,
                            modifier = Modifier.padding(
                                start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                                end = innerPadding.calculateEndPadding(LayoutDirection.Ltr),
                                bottom = innerPadding.calculateBottomPadding()
                            )
                        ) {
                            composable("notice") { NoticeScreen() }
                            composable("timetable") { TimetableScreen() }
                            composable("home") { HomeScreen() }
                            composable("weekly") { WeeklyMenuScreen() }
                            composable("schedule") { ScheduleScreen() }
                        }
                    }
                }

            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun AppPreview() {
    TodayMeal_MVVM_ComposeTheme() {
        val navController = rememberNavController()

        val noRippleConfiguration = RippleConfiguration(
            color = Color.Transparent,
            rippleAlpha = RippleAlpha(0f, 0f, 0f, 0f)
        )
        CompositionLocalProvider(LocalRippleConfiguration provides noRippleConfiguration) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                bottomBar = {
                    BottomAppBar(
                        navController) },
                floatingActionButton = {
                    HomeFab(
                        navController = navController) },
                floatingActionButtonPosition = FabPosition.Center
            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = HOME_ROUTE,
                    modifier = Modifier.padding(innerPadding)
                ) {
                    composable("notice") { NoticeScreen() }
                    composable("timetable") { TimetableScreen() }
                    composable("home") { HomeScreen() }
                    composable("weekly") { WeeklyMenuScreen() }
                    composable("schedule") { ScheduleScreen() }
                }
            }
        }

    }
}