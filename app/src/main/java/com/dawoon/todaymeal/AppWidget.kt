package com.dawoon.todaymeal

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.fonts.Font
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.Action
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.background
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontFamily
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.dawoon.todaymeal.di.WidgetEntryPoint
import com.dawoon.todaymeal.util.formatMealText
import dagger.hilt.android.EntryPointAccessors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.dawoon.todaymeal.network.ApiResult
import com.dawoon.todaymeal.network.model.MealRowDto
import com.dawoon.todaymeal.ui.theme.DarkBackground
import kotlin.collections.joinToString

val MEAL_TYPE_KEY = stringPreferencesKey("meal_type")

class AppWidget : GlanceAppWidget() {
    override val stateDefinition = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(
        context: Context,
        id: GlanceId
    ) {
        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            WidgetEntryPoint::class.java
        )
        val repository = entryPoint.schoolRepository()
        val pref = entryPoint.prefManager()

        val schoolCode = pref.getSchoolCode()
        val isSchoolNotSet = schoolCode.isEmpty()

        var breakfastText: String = ""
        var lunchText: String = ""
        var dinnerText: String = ""

//        val today = SimpleDateFormat("yyyyMMdd", Locale.KOREA).format(Date())
        val mockToday = "20250514"


        if (isSchoolNotSet) {
            breakfastText = "학교를 설정해주세요."
            lunchText = "학교를 설정해주세요."
            dinnerText = "학교를 설정해주세요."
        } else {
            val breakfastResult = repository.getMealServiceInfo(pref.getAtptCode(), pref.getSchoolCode(), "1", mockToday, mockToday)
            val lunchResult = repository.getMealServiceInfo(pref.getAtptCode(), pref.getSchoolCode(), "2", mockToday, mockToday)
            val dinnerResult = repository.getMealServiceInfo(pref.getAtptCode(), pref.getSchoolCode(), "3", mockToday, mockToday)

            breakfastText = getMealDisplayText(breakfastResult, "아침")
            lunchText = getMealDisplayText(lunchResult, "점심")
            dinnerText = getMealDisplayText(dinnerResult, "저녁")
        }


        provideContent {
            val prefs = currentState<Preferences>()
            val currentType = prefs[MEAL_TYPE_KEY] ?: "LUNCH"

            GlanceTheme {
                WidgetContent(currentType, breakfastText, lunchText, dinnerText)
            }
        }
    }

    private fun getMealDisplayText(result: ApiResult<List<MealRowDto>>, mealName: String): String {
        return when (result) {
            is ApiResult.Success -> {
                if (result.data.isEmpty()) {
                    "$mealName 정보가 없습니다."
                } else {
                    val menu = result.data.joinToString("\n") { it.DDISH_NM ?: "" }
                    formatMealText(menu)
                }
            }
            else -> "$mealName 정보가 없습니다."
        }
    }

    @Composable
    private fun WidgetContent(
        currentType: String,
        breakfastData: String,
        lunchData: String,
        dinnerData: String
    ) {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .padding(12.dp)
                .background(GlanceTheme.colors.background)
                .cornerRadius(16.dp)
        ) {

            Spacer(modifier = GlanceModifier.height(30.dp))

            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = " < ",
                    style = TextStyle(
                        GlanceTheme.colors.onSurface,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Medium,
                        fontSize = 30.sp
                    ),
                    modifier = GlanceModifier
                        .clickable(actionRunCallback<PrevMealAction>())
                )

                Spacer(modifier = GlanceModifier.width(20.dp))

                Image(
                    ImageProvider(R.drawable.icn_home),
                    contentDescription = "아이콘",
                    modifier = GlanceModifier.size(30.dp),
                )

                // 타이틀 결정
                val title = when (currentType) {
                    "MORNING" -> "아침 메뉴"
                    "LUNCH" -> "점심 메뉴"
                    else -> "저녁 메뉴"
                }
                Text(text = title,
                    style = TextStyle(
                        GlanceTheme.colors.onSurface,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                )

                Image(
                    ImageProvider(R.drawable.icn_home),
                    contentDescription = "아이콘",
                    modifier = GlanceModifier.size(30.dp),
                )

                Spacer(modifier = GlanceModifier.width(20.dp))

                Text(
                    text = " > ",
                    style = TextStyle(
                        GlanceTheme.colors.onSurface,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Medium,
                        fontSize = 30.sp
                    ),
                    modifier = GlanceModifier.clickable(actionRunCallback<NextMealAction>())
                )
            }


            // 식단 데이터 결정
            val displayData = when (currentType) {
                "MORNING" -> breakfastData
                "LUNCH" -> lunchData
                else -> dinnerData
            }

            Spacer(modifier = GlanceModifier.height(30.dp))

            LazyColumn(
                modifier = GlanceModifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally) {
                item {
                    Text(
                        text = displayData,
                        style = TextStyle(
                            GlanceTheme.colors.onSurface,
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Medium,
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center,
                        ),
                        modifier = GlanceModifier.fillMaxWidth())

                }
            }
        }
    }
}

// 다음 식단으로 (오른쪽 화살표용)
class NextMealAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) { prefs ->
            val current = prefs[MEAL_TYPE_KEY] ?: "LUNCH"
            val next = when (current) {
                "MORNING" -> "LUNCH"
                "LUNCH" -> "DINNER"
                "DINNER" -> "MORNING"
                else -> "LUNCH"
            }
            prefs.toMutablePreferences().apply { this[MEAL_TYPE_KEY] = next }
        }
        AppWidget().update(context, glanceId)
    }
}

// 이전 식단으로 (왼쪽 화살표용)
class PrevMealAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) { prefs ->
            val current = prefs[MEAL_TYPE_KEY] ?: "LUNCH"
            val prev = when (current) {
                "MORNING" -> "DINNER"   // 아침에서 왼쪽 누르면 저녁
                "LUNCH" -> "MORNING"   // 점심에서 왼쪽 누르면 아침
                "DINNER" -> "LUNCH"     // 저녁에서 왼쪽 누르면 점심
                else -> "LUNCH"
            }
            prefs.toMutablePreferences().apply { this[MEAL_TYPE_KEY] = prev }
        }
        AppWidget().update(context, glanceId)
    }
}

class RefreshAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        AppWidget().update(context, glanceId)
    }
}