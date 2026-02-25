package com.dawoon.todaymeal.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dawoon.todaymeal.R
import com.dawoon.todaymeal.ui.theme.DarkBackground
import com.dawoon.todaymeal.ui.theme.DarkText
import com.dawoon.todaymeal.ui.theme.DividerColor
import com.dawoon.todaymeal.ui.theme.LightText
import com.dawoon.todaymeal.ui.theme.TimetableColorList
import com.dawoon.todaymeal.viewmodel.TimetableViewModel

@Composable
fun TimetableScreen(
    viewModel: TimetableViewModel = hiltViewModel()
) {

    val days = listOf("월", "화", "수", "목", "금")
    val timetableData by viewModel.timetableState.collectAsState()
    val maxPeriod by viewModel.maxPeriod.collectAsState()
    val isDark = isSystemInDarkTheme()
    val textColor = if (isDark) DarkText else LightText
    val bgColor = if (isDark) DarkBackground else Color.White
    val timeTableTextColor = if (isDark) Color.White else Color.Black
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { viewModel.fetchTimetable("20250331", "20250404") }

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
                text = "시간표",
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

        Box(modifier = Modifier
            .fillMaxWidth()
            .height(0.5.dp)
            .padding(horizontal = 25.dp)
            .background(DividerColor))


        Box(modifier = Modifier.fillMaxSize()) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(if (isDark) DarkBackground else Color.White)
            ) {
                //고정된 상단 요일 헤더 (스크롤x)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 16.dp, start = 52.dp, end = 10.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    days.forEach { day ->
                        Text(
                            text = day,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = timeTableTextColor,
                            fontFamily = FontFamily(Font(R.font.suite_bold)),
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                //스크롤 가능한 시간표 영역
                LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(maxPeriod) { index ->
                        val period = index + 1
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        )
                        {
                            Text("$period",
                                modifier = Modifier.width(32.dp),
                                textAlign = TextAlign.Center,
                                color = timeTableTextColor,
                                fontSize = 18.sp,
                                fontFamily = FontFamily(Font(R.font.suite_bold)),
                                fontWeight = FontWeight.Bold)

                            (1..5).forEach { dayOfWeek ->
                                val foundSubject = timetableData.find {
                                    it.dayOfWeek == dayOfWeek && it.period == period
                                }?.subject

                                val subject = if (foundSubject.isNullOrEmpty()) "정보\n없음" else foundSubject

                                val isDataEmpty = foundSubject.isNullOrEmpty()

                                val colorIdx = ((dayOfWeek - 1) * 7) + ((period - 1) % 7)

                                TimetableCell(
                                    subject = subject,
                                    colorIndex = colorIdx,
                                    isDataEmpty = isDataEmpty, // 새로 추가할 파라미터
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize().background(bgColor.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = textColor)
                }
            }

            uiState.errorMessage?.let { msg ->
                Text(
                    text = msg,
                    modifier = Modifier.align(Alignment.Center),
                    textAlign = TextAlign.Center,
                    color = Color.Red
                )
            }

            uiState.infoMessage?.let { msg ->
                Text(
                    text = msg,
                    modifier = Modifier.align(Alignment.Center),
                    textAlign = TextAlign.Center,
                    color = textColor
                )
            }
        }

    }
}

@Composable
fun TimetableCell(
    subject: String,
    colorIndex: Int,
    isDataEmpty: Boolean,
    modifier: Modifier = Modifier
) {
    val baseColor = TimetableColorList[colorIndex % TimetableColorList.size]
    val isDark = isSystemInDarkTheme()

    val emptyColor = if (isDark) Color(0xFF3E3E3E) else Color(0xFFF2F2F2)
    val emptyTextColor = if (isDark) Color.Gray else Color.LightGray

    val dynamicFontSize = when {
        isDataEmpty -> 12.sp
        subject.length >= 6 -> 10.sp
        subject.length >= 4 -> 12.sp
        else -> 14.sp
    }

    Box(
        modifier = modifier
            .height(87.dp)
            .background(
                color = if (!isDataEmpty) baseColor else emptyColor,
                shape = RoundedCornerShape(14.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = subject,
            modifier = Modifier.padding(horizontal = 4.dp),
            fontSize = dynamicFontSize,
            color = if (isDataEmpty) emptyTextColor else Color.Black,
            fontWeight = if (isDataEmpty) FontWeight.Normal else FontWeight.Medium,
            fontFamily = FontFamily(Font(R.font.suite_semibold)),
            textAlign = TextAlign.Center,
            lineHeight = if (subject.length >= 4) 14.sp else 18.sp,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
    }
}