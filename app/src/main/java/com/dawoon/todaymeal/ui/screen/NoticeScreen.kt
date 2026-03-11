package com.dawoon.todaymeal.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dawoon.todaymeal.R
import com.dawoon.todaymeal.network.model.Notice
import com.dawoon.todaymeal.ui.theme.DarkBackground
import com.dawoon.todaymeal.ui.theme.DarkList
import com.dawoon.todaymeal.ui.theme.DarkSubBg
import com.dawoon.todaymeal.ui.theme.DarkText
import com.dawoon.todaymeal.ui.theme.LightBackground
import com.dawoon.todaymeal.ui.theme.LightList
import com.dawoon.todaymeal.ui.theme.LightText
import com.dawoon.todaymeal.util.dummyNotices

@Composable
fun NoticeScreen() {
    val isDark = isSystemInDarkTheme()
    val bgColor = if (isDark) DarkSubBg else LightBackground
    val textColor = if (isDark) DarkText else LightText
    val headerBg = if (isDark) DarkBackground else Color.White // 상태바/헤더 배경 (흰/검)
    var selectedNotice by remember { mutableStateOf<Notice?>(null) }

    Column(modifier =
        Modifier
            .fillMaxSize()
            .background(bgColor))
        {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsTopHeight(WindowInsets.statusBars) // 상태바 높이만큼 공간 확보
                    .background(headerBg) // 상태바 배경색 설정
            )

            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .background(if (isDark) DarkBackground else Color.White)
            ) {

                Text(text = stringResource(R.string.text_notice),
                    modifier =
                        Modifier
                            .align(Alignment.Center),
                    color = textColor,
                    fontSize = 24.sp,
                    fontFamily = FontFamily(
                        Font(resId = R.font.suite_extrabold)
                    ))

            }

            NoticeList(dummyNotices, { notice -> selectedNotice = notice }, isDark)
    }

    selectedNotice?.let { notice ->
        AlertDialog(
            onDismissRequest = { selectedNotice = null }, // 팝업 밖을 누르면 닫기
            confirmButton = {
                TextButton(onClick = { selectedNotice = null }) {
                    Text(stringResource(R.string.text_confirm), color = textColor, fontFamily = FontFamily(Font(R.font.suite_bold)))
                }
            },
            title = {
                Text(
                    text = notice.title,
                    fontFamily = FontFamily(Font(R.font.suite_extrabold)),
                    fontSize = 20.sp
                )
            },
            text = {
                Text(
                    text = notice.contents,
                    fontFamily = FontFamily(Font(R.font.suite_medium)),
                    fontSize = 16.sp
                )
            },
            containerColor = if (isDark) DarkBackground else Color.White,
            titleContentColor = textColor,
            textContentColor = textColor,
            shape = RoundedCornerShape(24.dp)
        )
    }
}

@Composable
fun NoticeList(
    notices: List<Notice>,
    onClick: (Notice) -> Unit,
    isDarkMode: Boolean
) {
    val bgColor = if (isDarkMode) DarkList else LightList
    val textColor = if (isDarkMode) DarkText else LightText

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        items(
            items = notices,
            key = { it.id }
        ) {
            notice ->

            Card(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .padding(top = 18.dp, start = 22.dp, end = 22.dp)
                        .clickable { onClick(notice) },
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(
                    containerColor = bgColor
                )
            ) {
                Text(
                    text = notice.title,
                    fontSize = 18.sp,
                    color = textColor,
                    modifier =
                        Modifier
                            .padding(top = 12.dp, start = 15.dp, end = 15.dp, bottom = 12.dp),
                    fontFamily = FontFamily(Font(resId = R.font.suite_bold))
                )
            }
        }
    }
}