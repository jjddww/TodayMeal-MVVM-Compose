package com.dawoon.todaymeal.ui.nav

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.dawoon.todaymeal.ui.screen.HomeScreen
import com.dawoon.todaymeal.ui.screen.NoticeScreen
import com.dawoon.todaymeal.ui.screen.ScheduleScreen
import com.dawoon.todaymeal.ui.screen.SettingScreen
import com.dawoon.todaymeal.ui.screen.TimetableScreen
import com.dawoon.todaymeal.ui.screen.WeeklyMenuScreen
import com.dawoon.todaymeal.viewmodel.SettingViewModel

fun NavGraphBuilder.appNavGraph(
    navController: NavController,
    onNavigateToHome: () -> Unit
) {
    composable("setting") {
        val viewModel: SettingViewModel = hiltViewModel()
        SettingScreen(
            viewModel = viewModel,
            onNavigateToNext = onNavigateToHome
        )
    }
    composable(HOME_ROUTE) { HomeScreen(navController = navController) }
    composable(BottomItem.Notice.route) { NoticeScreen() }
    composable(BottomItem.Timetable.route) { TimetableScreen() }
    composable(BottomItem.WeeklyMenu.route) { WeeklyMenuScreen() }
    composable(BottomItem.Schedule.route) { ScheduleScreen() }
}