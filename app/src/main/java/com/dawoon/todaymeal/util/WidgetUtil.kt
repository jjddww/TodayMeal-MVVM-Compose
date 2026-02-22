package com.dawoon.todaymeal.util

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import com.dawoon.todaymeal.AppWidget

object WidgetUtil {
    /**
     * 위젯을 즉시 강제 업데이트
     */
    suspend fun updateAllWidgets(context: Context) {
        try {
            val manager = GlanceAppWidgetManager(context)
            val glanceIds = manager.getGlanceIds(AppWidget::class.java)

            glanceIds.forEach { glanceId ->
                updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) { prefs ->
                    val mutablePrefs = prefs.toMutablePreferences()
                    mutablePrefs[stringPreferencesKey("force_update_key")] = System.currentTimeMillis().toString()
                    mutablePrefs
                }

                AppWidget().update(context, glanceId)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}