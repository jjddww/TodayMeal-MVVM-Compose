package com.dawoon.todaymeal.ui.screen

import android.R
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dawoon.todaymeal.network.model.Notice
import com.dawoon.todaymeal.ui.theme.DarkBackground
import com.dawoon.todaymeal.ui.theme.DarkList
import com.dawoon.todaymeal.ui.theme.DarkText
import com.dawoon.todaymeal.ui.theme.LightBackground
import com.dawoon.todaymeal.ui.theme.LightList
import com.dawoon.todaymeal.ui.theme.LightText

@Composable
fun NoticeScreen() {
    val isDark = isSystemInDarkTheme()
    val bgColor = if (isDark) DarkBackground else LightBackground

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(bgColor)) {
        val dummyNotices = listOf(
            Notice(
                id = 1,
                title = "서비스 점검 안내",
                contents = "안정적인 서비스 제공을 위해 2월 10일 02:00~04:00까지 시스템 점검이 진행됩니다."
            ),
            Notice(
                id = 2,
                title = "신규 기능 업데이트",
                contents = "알림 설정 기능이 추가되었습니다. 설정 화면에서 자유롭게 관리할 수 있습니다."
            ),
            Notice(
                id = 3,
                title = "이용약관 변경 안내",
                contents = "관련 법령 개정에 따라 이용약관이 일부 변경됩니다. 자세한 내용은 공지사항을 확인해주세요."
            )
        )

        NoticeList(dummyNotices, {}, isDark)
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
                    color = textColor
                )
            }
        }
    }
}