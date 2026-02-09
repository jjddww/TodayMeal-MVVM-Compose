package com.dawoon.todaymeal.ui.nav

import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import com.dawoon.todaymeal.R

sealed class BottomItem(
    val route: String,
    val label: String,
    @DrawableRes val light: Int,
    @DrawableRes val dark: Int,
    @DrawableRes val selectedLight: Int,
    @DrawableRes val selectedDark: Int
) {
    data object Notice : BottomItem(
        "notice", "공지사항",
        R.drawable.icn_notice_light,
        R.drawable.icn_notice_dark,
        R.drawable.icn_notice_dark,
        R.drawable.icn_notice_light
    )

    data object Timetable : BottomItem(
        "timetable", "시간표",
        R.drawable.icn_timetable_light,
        R.drawable.icn_timetable_dark,
        R.drawable.icn_timetable_dark,
        R.drawable.icn_timetable_light
    )

    data object WeeklyMenu : BottomItem(
        "weekly", "주간급식",
        R.drawable.icn_lunch_light,
        R.drawable.icn_lunch_dark,
        R.drawable.icn_lunch_dark,
        R.drawable.icn_lunch_light
    )

    data object Schedule : BottomItem(
        "schedule", "일정",
        R.drawable.icn_schedule_light,
        R.drawable.icn_schedule_dark,
        R.drawable.icn_schedule_dark,
        R.drawable.icn_schedule_light
    )
}

val bottomItems = listOf(
    BottomItem.Notice,
    BottomItem.Timetable,
    BottomItem.WeeklyMenu,
    BottomItem.Schedule
)

const val HOME_ROUTE = "home"