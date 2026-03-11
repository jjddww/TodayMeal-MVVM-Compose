package com.dawoon.todaymeal.ui.etc

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dawoon.todaymeal.R
import com.dawoon.todaymeal.ui.theme.DarkText
import com.dawoon.todaymeal.ui.theme.LightText

@Composable
fun NutritionDialog(
    carInfo: String?,
    nutritionInfo: String?,
    onDismiss: () -> Unit
) {

    val isDark = isSystemInDarkTheme()
    val txtColor = if (isDark) DarkText else LightText

    val formattedInfo = if (nutritionInfo.isNullOrBlank()) {
        stringResource(R.string.nutrition_empty_day_title)
    } else {
        nutritionInfo.replace("<br/>", "\n")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.nutrition_dialog_title),
                fontSize = 20.sp,
                fontFamily = FontFamily(Font(R.font.suite_bold)),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(horizontal = 5.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = carInfo ?: stringResource(R.string.empty_info_dialog),
                    fontSize = 16.sp,
                    lineHeight = 22.sp,
                    fontFamily = FontFamily(Font(R.font.suite_medium)),
                    textAlign = TextAlign.Center,
                    color = txtColor
                )

                Spacer(
                    modifier = Modifier
                        .height(10.dp)
                )

                Text(
                    text = formattedInfo,
                    fontSize = 16.sp,
                    lineHeight = 22.sp,
                    fontFamily = FontFamily(Font(R.font.suite_medium)),
                    textAlign = TextAlign.Center,
                    color = txtColor
                )
            }
        },
        confirmButton = {
            Text(
                text = stringResource(R.string.text_close),
                color = txtColor,
                fontFamily = FontFamily(Font(R.font.suite_bold)),
                modifier = Modifier
                    .padding(16.dp)
                    .clickable { onDismiss() }
            )
        }
    )
}