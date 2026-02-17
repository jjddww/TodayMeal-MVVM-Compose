package com.dawoon.todaymeal.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dawoon.todaymeal.R
import com.dawoon.todaymeal.ui.etc.MealTypeTabs
import com.dawoon.todaymeal.ui.etc.WeekChipRow
import com.dawoon.todaymeal.ui.etc.WeeklyMealList
import com.dawoon.todaymeal.ui.theme.DarkBackground
import com.dawoon.todaymeal.ui.theme.DarkText
import com.dawoon.todaymeal.ui.theme.LightText
import com.dawoon.todaymeal.viewmodel.WeeklyMenuViewModel

@Composable
fun WeeklyMenuScreen(viewModel: WeeklyMenuViewModel = hiltViewModel()) {
    val isDark = isSystemInDarkTheme()
    val textColor = if (isDark) DarkText else LightText
    val bgColor = if (isDark) DarkBackground else Color.White

    val weeks by viewModel.weeks.collectAsStateWithLifecycle()
    val selectedIndex by viewModel.selectedWeekIndex.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    LaunchedEffect(selectedIndex) {
        listState.animateScrollToItem(selectedIndex)
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsTopHeight(WindowInsets.statusBars)
        )


        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "주간 메뉴",
            fontSize = 24.sp,
            fontFamily = FontFamily(Font(R.font.suite_extrabold)),
            color = textColor,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(30.dp))

        MealTypeTabs(
            selectedType = viewModel.selectedMealType,
            onTypeSelected = { viewModel.updateMealType(it) }
        )

        Spacer(modifier = Modifier.height(15.dp))


        WeekChipRow(
            weeks = weeks,
            selectedIndex = selectedIndex,
            onWeekSelected = { viewModel.updateWeekIndex(it) }
        )

        Spacer(modifier = Modifier.height(15.dp))

        if (uiState.loading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF639D7C))
            }
        } else {
            WeeklyMealList(meals = uiState.items)
        }

    }

}
