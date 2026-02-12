package com.dawoon.todaymeal.ui.etc

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dawoon.todaymeal.R
import com.dawoon.todaymeal.network.model.MealRowDto
import com.dawoon.todaymeal.network.model.WeekRange
import com.dawoon.todaymeal.ui.theme.DarkChipSelected
import com.dawoon.todaymeal.ui.theme.DarkChipUnselected
import com.dawoon.todaymeal.ui.theme.DarkList
import com.dawoon.todaymeal.ui.theme.DarkText
import com.dawoon.todaymeal.ui.theme.LightChipSelected
import com.dawoon.todaymeal.ui.theme.LightChipUnselected
import com.dawoon.todaymeal.ui.theme.LightList
import com.dawoon.todaymeal.ui.theme.LightSubBg
import com.dawoon.todaymeal.ui.theme.LightText
import com.dawoon.todaymeal.util.DateCalculator.formatDisplayDate
import com.dawoon.todaymeal.util.formatMealText

@Composable
fun MealTypeTabs(
    selectedType: String,
    onTypeSelected: (String) -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val textColor = if (isDark) DarkText else LightText
    val titles = listOf("아침", "점심", "저녁")
    val types = listOf("1", "2", "3") // API 코드와 매칭

    TabRow(
        selectedTabIndex = types.indexOf(selectedType),
        containerColor = Color.Transparent,
        contentColor = Color.White,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                Modifier.tabIndicatorOffset(tabPositions[types.indexOf(selectedType)]),
                height = 1.dp,
                color = if (isDark) Color.White else Color.Black
            )
        },
        divider = {} // 구분선 제거
    ) {
        titles.forEachIndexed { index, title ->
            Tab(
                selected = selectedType == types[index],
                onClick = { onTypeSelected(types[index]) },
                text = {
                    val currentTabColor = if (selectedType == types[index]) {
                        if (isDark) LightSubBg else LightText
                    } else {
                        if (isDark) LightText else LightSubBg
                    }

                    Text(
                        text = title,
                        color = currentTabColor, // 계산된 색상 적용
                        fontSize = 22.sp,
                        fontFamily = FontFamily(Font(R.font.suite_extrabold))
                    )
                }
            )
        }
    }
}


@Composable
fun WeekChipRow(
    weeks: List<WeekRange>,
    selectedIndex: Int,
    onWeekSelected: (Int) -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val selectedChipColor = if (isDark) DarkChipSelected else LightChipSelected
    val unselectedChipColor = if (isDark) DarkChipUnselected else LightChipUnselected

    LazyRow(
        contentPadding = PaddingValues(horizontal = 17.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        itemsIndexed(weeks) { index, week ->
            val isSelected = index == selectedIndex
            Box(
                modifier = Modifier
                    .width(90.dp)
                    .height(50.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(
                        width = 1.dp,
                        color = if (isSelected) selectedChipColor else unselectedChipColor,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .clickable { onWeekSelected(index) }
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = week.weekLabel,
                    color = if (isSelected) selectedChipColor else unselectedChipColor,
                    fontSize = 20.sp,
                    fontFamily = FontFamily(Font(R.font.suite_extrabold))
                )
            }
        }
    }
}

@Composable
fun WeeklyMealList(
    meals: List<MealRowDto>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 0.dp, bottom = 48.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(items = meals) { meal ->
            WeeklyMealCard(meal = meal)
        }
    }
}

@Composable
fun WeeklyMealCard(meal: MealRowDto) {
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
                text = formatDisplayDate(meal.MLSV_YMD),
                color = dateColor,
                fontSize = 20.sp,
                fontFamily = FontFamily(Font(R.font.suite_semibold))
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = formatMealText(meal.DDISH_NM ?: "정보 없음"),
                color = menuColor,
                fontSize = 20.sp,
                lineHeight = 26.sp,
                textAlign = TextAlign.Center,
                fontFamily = FontFamily(Font(R.font.suite_semibold))
            )
        }
    }
}