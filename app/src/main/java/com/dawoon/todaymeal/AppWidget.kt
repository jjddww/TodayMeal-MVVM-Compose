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
import kotlinx.coroutines.runBlocking
import kotlin.collections.joinToString

val MEAL_TYPE_KEY = stringPreferencesKey("meal_type")

class AppWidget : GlanceAppWidget() {
    override val stateDefinition = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        // Hilt 진입점 설정
        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            WidgetEntryPoint::class.java
        )
        val repository = entryPoint.schoolRepository()
        val pref = entryPoint.prefManager()

        provideContent {
            val prefs = currentState<Preferences>()
            val currentType = prefs[MEAL_TYPE_KEY] ?: "LUNCH"

            val schoolCode = runBlocking { pref.getSchoolCode() }
            val atptCode = runBlocking { pref.getAtptCode() }

            val mockToday = "20250514"
            val today = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())


            var breakfastText = "정보가 없습니다."
            var lunchText = "정보가 없습니다."
            var dinnerText = "정보가 없습니다."

            if (schoolCode.isEmpty()) {
                breakfastText = "학교를 설정해주세요."
                lunchText = "학교를 설정해주세요."
                dinnerText = "학교를 설정해주세요."
            } else {
                val bRes = runBlocking { repository.getMealServiceInfo(atptCode, schoolCode, "1", mockToday, mockToday) }
                val lRes = runBlocking { repository.getMealServiceInfo(atptCode, schoolCode, "2", mockToday, mockToday) }
                val dRes = runBlocking { repository.getMealServiceInfo(atptCode, schoolCode, "3", mockToday, mockToday) }

                breakfastText = getMealDisplayText(bRes, "아침")
                lunchText = getMealDisplayText(lRes, "점심")
                dinnerText = getMealDisplayText(dRes, "저녁")
            }

            GlanceTheme {
                WidgetContent(currentType, breakfastText, lunchText, dinnerText)
            }
        }
    }

    private fun getMealDisplayText(result: ApiResult<List<MealRowDto>>, mealName: String): String {
        return when (result) {
            is ApiResult.Success -> {
                if (result.data.isEmpty()) "$mealName 정보가 없습니다."
                else formatMealText(result.data.joinToString("\n") { it.DDISH_NM ?: "" })
            }
            else -> "$mealName 정보가 없습니다."
        }
    }

    @Composable
    private fun WidgetContent(currentType: String, breakfastData: String, lunchData: String, dinnerData: String) {
        Column(
            modifier = GlanceModifier.fillMaxSize().padding(12.dp)
                .background(GlanceTheme.colors.background).cornerRadius(16.dp)
        ) {
            Spacer(modifier = GlanceModifier.height(30.dp))
            Row(modifier = GlanceModifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalAlignment = Alignment.CenterVertically) {
                Text(text = " < ", style = TextStyle(color = GlanceTheme.colors.onSurface, fontSize = 30.sp, fontWeight = FontWeight.Medium),
                    modifier = GlanceModifier.clickable(actionRunCallback<PrevMealAction>()))

                Spacer(modifier = GlanceModifier.width(20.dp))
                Image(ImageProvider(R.drawable.icn_home), contentDescription = null, modifier = GlanceModifier.size(30.dp))

                val title = when (currentType) {
                    "MORNING" -> "아침 메뉴"
                    "LUNCH" -> "점심 메뉴"
                    else -> "저녁 메뉴"
                }
                Text(text = title, style = TextStyle(color = GlanceTheme.colors.onSurface, fontSize = 20.sp, fontWeight = FontWeight.Bold))

                Image(ImageProvider(R.drawable.icn_home), contentDescription = null, modifier = GlanceModifier.size(30.dp))
                Spacer(modifier = GlanceModifier.width(20.dp))

                Text(text = " > ", style = TextStyle(color = GlanceTheme.colors.onSurface, fontSize = 30.sp, fontWeight = FontWeight.Medium),
                    modifier = GlanceModifier.clickable(actionRunCallback<NextMealAction>()))
            }

            val displayData = when (currentType) {
                "MORNING" -> breakfastData
                "LUNCH" -> lunchData
                else -> dinnerData
            }

            Spacer(modifier = GlanceModifier.height(30.dp))
            LazyColumn(modifier = GlanceModifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                item {
                    Text(text = displayData, style = TextStyle(color = GlanceTheme.colors.onSurface, fontSize = 18.sp, textAlign = TextAlign.Center),
                        modifier = GlanceModifier.fillMaxWidth())
                }
            }
        }
    }
}


class NextMealAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) { prefs ->
            val current = prefs[MEAL_TYPE_KEY] ?: "LUNCH"
            val next = when (current) { "MORNING" -> "LUNCH"; "LUNCH" -> "DINNER"; "DINNER" -> "MORNING"; else -> "LUNCH" }
            prefs.toMutablePreferences().apply { this[MEAL_TYPE_KEY] = next }
        }
        AppWidget().update(context, glanceId)
    }
}

class PrevMealAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) { prefs ->
            val current = prefs[MEAL_TYPE_KEY] ?: "LUNCH"
            val prev = when (current) { "MORNING" -> "DINNER"; "LUNCH" -> "MORNING"; "DINNER" -> "LUNCH"; else -> "LUNCH" }
            prefs.toMutablePreferences().apply { this[MEAL_TYPE_KEY] = prev }
        }
        AppWidget().update(context, glanceId)
    }
}