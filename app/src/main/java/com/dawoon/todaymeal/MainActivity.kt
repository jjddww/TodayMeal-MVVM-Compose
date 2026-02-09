package com.dawoon.todaymeal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dawoon.todaymeal.ui.screen.HomeScreen
import com.dawoon.todaymeal.ui.screen.NoticeScreen
import com.dawoon.todaymeal.ui.screen.ScheduleScreen
import com.dawoon.todaymeal.ui.screen.TimetableScreen
import com.dawoon.todaymeal.ui.screen.WeeklyMenuScreen
import com.dawoon.todaymeal.ui.theme.TodayMeal_MVVM_ComposeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TodayMeal_MVVM_ComposeTheme() {
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