package com.dawoon.todaymeal.ui.etc

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dawoon.todaymeal.R
import com.dawoon.todaymeal.network.model.MealRowDto
import com.dawoon.todaymeal.ui.theme.DarkList
import com.dawoon.todaymeal.ui.theme.DarkText
import com.dawoon.todaymeal.ui.theme.LightList
import com.dawoon.todaymeal.ui.theme.LightText
import com.dawoon.todaymeal.util.formatMealText

@Composable
fun MealCardView(
    meal: MealRowDto,
    modifier: Modifier = Modifier
) {

    val isDark = isSystemInDarkTheme()
    val cardColor = if (isDark) DarkList else LightList
    val textColor = if (isDark) DarkText else LightText


    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = formatMealText(meal.DDISH_NM ?: "정보가 없습니다"),
                textAlign = TextAlign.Center,
                color = textColor,
                fontSize = 19.sp,
                fontFamily = FontFamily(Font(resId = R.font.suite_bold)),
                lineHeight = 28.sp
            )
        }

    }
}