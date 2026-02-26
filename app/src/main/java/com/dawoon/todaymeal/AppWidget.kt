package com.dawoon.todaymeal

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.fonts.Font
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
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
import androidx.glance.action.actionStartActivity
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
        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            WidgetEntryPoint::class.java
        )
        val repository = entryPoint.schoolRepository()

        provideContent {
            // 1. 위젯 자신의 상태(currentState)에서 직접 학교 코드 추출
            val prefs = currentState<Preferences>()
            val schoolCode = prefs[stringPreferencesKey("WIDGET_SCHOOL_CODE")] ?: ""
            val atptCode = prefs[stringPreferencesKey("WIDGET_ATPT_CODE")] ?: ""
            val currentType = prefs[MEAL_TYPE_KEY] ?: "LUNCH"

            val mockToday = "20250514"

            // 2. 식단 데이터 로드 (suspend 호출을 위해 runBlocking 대신
            // 위젯의 특수한 데이터 흐름을 활용하거나 간단히 처리)
            // 사실 provideGlance 레벨에서 데이터를 가져오는게 좋으므로 구조를 살짝 틉니다.

            val breakfastText = if (schoolCode.isEmpty()) "학교를 설정해주세요."
            else runBlocking {
                WidgetMealMapper.getMealDisplayText(
                    repository.getMealServiceInfo(
                        atptCode,
                        schoolCode,
                        "1",
                        mockToday,
                        mockToday
                    ), "아침"
                )
            }

            val lunchText = if (schoolCode.isEmpty()) "학교를 설정해주세요."
            else runBlocking {
                WidgetMealMapper.getMealDisplayText(
                    repository.getMealServiceInfo(
                        atptCode,
                        schoolCode,
                        "2",
                        mockToday,
                        mockToday
                    ), "점심"
                )
            }

            val dinnerText = if (schoolCode.isEmpty()) "학교를 설정해주세요."
            else runBlocking {
                WidgetMealMapper.getMealDisplayText(
                    repository.getMealServiceInfo(
                        atptCode,
                        schoolCode,
                        "3",
                        mockToday,
                        mockToday
                    ), "저녁"
                )
            }

            GlanceTheme {
                WidgetContent(currentType, breakfastText, lunchText, dinnerText)
            }
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
            modifier = GlanceModifier.fillMaxSize().padding(12.dp)
                .background(GlanceTheme.colors.background).cornerRadius(16.dp)
        ) {
            Spacer(modifier = GlanceModifier.height(20.dp))


            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Image(
                    provider = ImageProvider(R.drawable.ic_widget_left_arrow),
                    contentDescription = "left arrow",
                    modifier = GlanceModifier
                        .size(30.dp)
                        .clickable(actionRunCallback<PrevMealAction>()),
                    colorFilter = ColorFilter.tint(GlanceTheme.colors.onSurface)
                )


                Spacer(modifier = GlanceModifier.width(10.dp))


                Image(
                    provider = ImageProvider(R.drawable.icn_home),
                    contentDescription = null,
                    modifier = GlanceModifier.size(25.dp)
                )

                Spacer(modifier = GlanceModifier.width(8.dp))

                val title = when (currentType) {
                    "MORNING" -> "아침 메뉴"
                    "LUNCH" -> "점심 메뉴"
                    else -> "저녁 메뉴"
                }
                Text(
                    text = title,
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurface,
                        fontSize = 18.sp,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = GlanceModifier.width(8.dp))


                Image(
                    provider = ImageProvider(R.drawable.icn_home),
                    contentDescription = null,
                    modifier = GlanceModifier.size(25.dp)
                )

                Spacer(modifier = GlanceModifier.width(10.dp))


                Image(
                    provider = ImageProvider(R.drawable.ic_widget_right_arrow),
                    contentDescription = "left arrow",
                    modifier = GlanceModifier
                        .size(30.dp)
                        .clickable(actionRunCallback<NextMealAction>()),
                    colorFilter = ColorFilter.tint(GlanceTheme.colors.onSurface)
                )
            }

            Spacer(modifier = GlanceModifier.height(16.dp))

            val displayData = when (currentType) {
                "MORNING" -> breakfastData
                "LUNCH" -> lunchData
                else -> dinnerData
            }


            Box(modifier = GlanceModifier.defaultWeight().fillMaxWidth()) {
                LazyColumn(modifier = GlanceModifier.fillMaxSize()) {
                    item {
                        Text(
                            text = displayData,
                            style = TextStyle(
                                fontFamily = FontFamily.SansSerif,
                                color = GlanceTheme.colors.onSurface,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center
                            ),
                            modifier = GlanceModifier.fillMaxWidth().padding(bottom = 30.dp)
                        )
                    }
                }
            }
        }
    }
}

object WidgetMealMapper {
    fun getNextMealType(current: String): String = when (current) {
        "MORNING" -> "LUNCH"
        "LUNCH" -> "DINNER"
        "DINNER" -> "MORNING"
        else -> "LUNCH"
    }

    fun getPrevMealType(current: String): String = when (current) {
        "MORNING" -> "DINNER"
        "LUNCH" -> "MORNING"
        "DINNER" -> "LUNCH"
        else -> "LUNCH"
    }

    fun getMealDisplayText(result: ApiResult<List<MealRowDto>>, mealName: String): String {
        return when (result) {
            is ApiResult.Success -> {
                if (result.data.isEmpty()) "$mealName 정보가 없습니다."
                else formatMealText(result.data.joinToString("\n") { it.DDISH_NM ?: "" })
            }
            else -> "$mealName 정보가 없습니다."
        }
    }
}


class NextMealAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) { prefs ->
            val current = prefs[MEAL_TYPE_KEY] ?: "LUNCH"
            val next = WidgetMealMapper.getNextMealType(current)
            prefs.toMutablePreferences().apply { this[MEAL_TYPE_KEY] = next }
        }
        AppWidget().update(context, glanceId)
    }
}

class PrevMealAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) { prefs ->
            val current = prefs[MEAL_TYPE_KEY] ?: "LUNCH"
            val prev = WidgetMealMapper.getPrevMealType(current)
            prefs.toMutablePreferences().apply { this[MEAL_TYPE_KEY] = prev }
        }
        AppWidget().update(context, glanceId)
    }
}