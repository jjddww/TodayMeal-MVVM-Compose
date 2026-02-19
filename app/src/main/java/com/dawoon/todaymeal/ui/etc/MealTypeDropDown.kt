package com.dawoon.todaymeal.ui.etc

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dawoon.todaymeal.R
import com.dawoon.todaymeal.ui.theme.DarkText
import com.dawoon.todaymeal.ui.theme.LightText

@Composable
fun MealTypeDropdown(
    selectedType: String, // ViewModel에서 온 값
    onTypeSelected: (String) -> Unit // 변경 시 알림
) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf("아침", "점심", "저녁")

    val displayName = when(selectedType) {
        "1" -> "아침"
        "2" -> "점심"
        "3" -> "저녁"
        else -> "점심"
    }
    val isDark = isSystemInDarkTheme()
    val textColor = if (isDark) DarkText else LightText


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize(Alignment.TopCenter)
    ) {
        Row(
            modifier = Modifier
                .clickable { expanded = true }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.icn_meal_drop_down),
                contentDescription = null,
                tint = textColor)
            Text(text = displayName,
                fontSize = 22.sp,
                modifier = Modifier.padding(start = 6.dp),
                fontFamily = FontFamily(Font(resId = R.font.suite_bold)),
                color = textColor
            )
        }

        // 4. 실제 드롭다운 메뉴
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onTypeSelected(option) // 외부(ViewModel)로 변경 알림
                        expanded = false
                    }
                )
            }
        }
    }
}