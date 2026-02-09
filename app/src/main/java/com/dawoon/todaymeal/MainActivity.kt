package com.dawoon.todaymeal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dawoon.todaymeal.ui.nav.BottomAppBar
import com.dawoon.todaymeal.ui.nav.HOME_ROUTE
import com.dawoon.todaymeal.ui.nav.HomeFab
import com.dawoon.todaymeal.ui.nav.rememberBottomBarHeightState
import com.dawoon.todaymeal.ui.screen.HomeScreen
import com.dawoon.todaymeal.ui.screen.NoticeScreen
import com.dawoon.todaymeal.ui.screen.ScheduleScreen
import com.dawoon.todaymeal.ui.screen.TimetableScreen
import com.dawoon.todaymeal.ui.screen.WeeklyMenuScreen
import com.dawoon.todaymeal.ui.theme.TodayMeal_MVVM_ComposeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TodayMeal_MVVM_ComposeTheme() {
                val navController = rememberNavController()
                val bottomBarState = rememberBottomBarHeightState()

                val noRippleConfiguration = RippleConfiguration(
                    color = Color.Transparent,
                    rippleAlpha = RippleAlpha(0f, 0f, 0f, 0f)
                )
                CompositionLocalProvider(LocalRippleConfiguration provides noRippleConfiguration) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        bottomBar = {
                            BottomAppBar(
                                navController,
                                onHeightPxChanged = bottomBarState.onHeightPxChanged)
                        } ,
                        floatingActionButton = {
                            HomeFab(
                                navController = navController,
                                bottomBarHeight = bottomBarState.height) },
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
    }
}


@Preview(showBackground = true)
@Composable
fun AppPreview() {
    TodayMeal_MVVM_ComposeTheme {
        val navController = rememberNavController()

        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "home",
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