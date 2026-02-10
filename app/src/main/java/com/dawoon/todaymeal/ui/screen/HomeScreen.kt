package com.dawoon.todaymeal.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dawoon.todaymeal.R
import com.dawoon.todaymeal.ui.theme.DarkBackground
import com.dawoon.todaymeal.ui.theme.DarkText
import com.dawoon.todaymeal.ui.theme.LightText
import com.dawoon.todaymeal.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val isDark = isSystemInDarkTheme()
    val textColor = if (isDark) DarkText else LightText

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .background(if (isDark) DarkBackground else Color.White)
        ) {

            Text(text = "고등학교",
                modifier =
                    Modifier
                        .align(Alignment.Center),
                color = textColor,
                fontSize = 24.sp,
                fontFamily = FontFamily(
                    Font(resId = R.font.suite_extrabold)
                ))

        }
    }
}
