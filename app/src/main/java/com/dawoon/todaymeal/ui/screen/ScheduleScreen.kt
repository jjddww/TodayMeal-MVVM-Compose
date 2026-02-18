package com.dawoon.todaymeal.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dawoon.todaymeal.R
import com.dawoon.todaymeal.ui.etc.ScheduleList
import com.dawoon.todaymeal.ui.theme.DarkBackground
import com.dawoon.todaymeal.ui.theme.DarkList
import com.dawoon.todaymeal.ui.theme.DarkSubBg
import com.dawoon.todaymeal.ui.theme.DarkText
import com.dawoon.todaymeal.ui.theme.LightBackground
import com.dawoon.todaymeal.ui.theme.LightList
import com.dawoon.todaymeal.ui.theme.LightSubBg
import com.dawoon.todaymeal.ui.theme.LightText
import com.dawoon.todaymeal.util.DateCalculator
import com.dawoon.todaymeal.viewmodel.ScheduleViewModel
import kotlinx.coroutines.launch

@Composable
fun ScheduleScreen(
    viewModel: ScheduleViewModel = hiltViewModel()
) {
    val scheduleState by viewModel.state.collectAsState()
    val displayMonth = viewModel.displayMonth

    val isDark = isSystemInDarkTheme()
    val textColor = if (isDark) DarkText else LightText
    val bgColor = if (isDark) DarkBackground else Color.White

    Column(modifier =
        Modifier
            .fillMaxSize()
            .background(bgColor)) {

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsTopHeight(WindowInsets.statusBars)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .background(color = bgColor)
        ) {

            Text(
                text = "학사일정",
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center),
                color = textColor,
                fontSize = 24.sp,
                fontFamily = FontFamily(
                    Font(resId = R.font.suite_extrabold)
                ),
                textAlign = TextAlign.Center
            )
        }


        Row(
            modifier =
                Modifier.padding(horizontal = 18.dp),
        ) {

            Icon(
                painter = painterResource(id = R.drawable.icn_left_arrow),
                contentDescription = "left arrow",
                tint = textColor,
                modifier = Modifier
                    .size(25.dp)
                    .clickable {
                        viewModel.moveToPreviousMonth()
                    }
            )

            Text(
                text = displayMonth,
                color = textColor,
                fontSize = 22.sp,
                fontFamily = FontFamily(
                    Font(resId = R.font.suite_bold)
                ),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )


            Icon(
                painter = painterResource(id = R.drawable.icn_right_arrow),
                contentDescription = "left arrow",
                tint = textColor,
                modifier = Modifier
                    .size(25.dp)
                    .clickable {
                        viewModel.moveToNextMonth()
                    }
            )

        }

        Spacer(modifier = Modifier.height(30.dp))

        Box(modifier = Modifier.fillMaxSize()) {
            if (scheduleState.loading) {
                 CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (scheduleState.items.isEmpty()) {
                Text(
                    text = "해당 월에 학사일정이 없습니다.",
                    modifier = Modifier.align(Alignment.Center),
                    color = textColor.copy(alpha = 0.5f),
                    fontFamily = FontFamily(Font(R.font.suite_semibold))
                )
            } else {
                // 결과 리스트 표시
                ScheduleList(
                    schedules = scheduleState.items
                )
            }
        }
    }
}
