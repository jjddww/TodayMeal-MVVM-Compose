package com.dawoon.todaymeal.ui.etc

import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dawoon.todaymeal.R
import com.dawoon.todaymeal.network.model.SchoolScheduleRowDto
import com.dawoon.todaymeal.ui.theme.DarkChipSelected
import com.dawoon.todaymeal.ui.theme.DarkList
import com.dawoon.todaymeal.ui.theme.LightChipSelected
import com.dawoon.todaymeal.ui.theme.LightList
import com.dawoon.todaymeal.ui.theme.LightText
import com.dawoon.todaymeal.util.DateCalculator.formatDisplayDate
import com.dawoon.todaymeal.util.formatMealText

@Composable
fun ScheduleList(
    schedules: List<SchoolScheduleRowDto>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 0.dp, bottom = 48.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(items = schedules) { schedule ->
            ScheduleCard(schedule)
        }
    }
}

@Composable
fun ScheduleCard(schedule: SchoolScheduleRowDto) {
    val isDark = isSystemInDarkTheme()
    val bgColor = if (isDark) DarkList else LightList
    val dateColor = if (isDark) DarkChipSelected else LightChipSelected
    val menuColor = if (isDark) Color.White else Color.Black

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = LightText,
                shape = RoundedCornerShape(24.dp)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Column(
            modifier = Modifier
                .padding(25.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = formatDisplayDate(schedule.AA_YMD),
                color = dateColor,
                fontSize = 20.sp,
                fontFamily = FontFamily(Font(R.font.suite_semibold))
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = formatMealText(schedule.EVENT_NM ?: "정보 없음"),
                color = menuColor,
                fontSize = 20.sp,
                lineHeight = 26.sp,
                textAlign = TextAlign.Center,
                fontFamily = FontFamily(Font(R.font.suite_semibold))
            )
        }
    }
}