package com.dawoon.todaymeal.util

import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PreferenceManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    // 위젯(AppWidget.kt)에서 사용하는 키와 반드시 동일하게 맞춤
    companion object {
        val ATPT_CODE = stringPreferencesKey("WIDGET_ATPT_CODE")
        val SCHOOL_CODE = stringPreferencesKey("WIDGET_SCHOOL_CODE")
        val SCHOOL_NAME = stringPreferencesKey("SCHOOL_NAME")
        val SCHOOL_TYPE = stringPreferencesKey("SCHOOL_TYPE")
        val USER_GRADE = stringPreferencesKey("USER_GRADE")
        val USER_CLASS = stringPreferencesKey("USER_CLASS")
    }

    // 저장 로직 (suspend 함수로 변경)
    suspend fun saveSchool(atpt: String, code: String, name: String) {
        dataStore.edit { prefs ->
            prefs[ATPT_CODE] = atpt
            prefs[SCHOOL_CODE] = code
            prefs[SCHOOL_NAME] = name
            prefs[SCHOOL_TYPE] = when {
                name.contains("초등학교") -> "ELEMENTARY"
                name.contains("중학교") -> "MIDDLE"
                name.contains("고등학교") -> "HIGH"
                else -> ""
            }
        }
    }

    suspend fun saveGradeAndClass(grade: String, classNum: String) {
        dataStore.edit { prefs ->
            prefs[USER_GRADE] = grade
            prefs[USER_CLASS] = classNum
        }
    }

    suspend fun clearAll() {
        dataStore.edit { it.clear() }
    }

    // 질문하신 Getter 함수들 (suspend 버전)
    suspend fun getAtptCode(): String = dataStore.data.map { it[ATPT_CODE] ?: "" }.first()
    suspend fun getSchoolCode(): String = dataStore.data.map { it[SCHOOL_CODE] ?: "" }.first()
    suspend fun getSchoolName(): String = dataStore.data.map { it[SCHOOL_NAME] ?: "" }.first()
    suspend fun getSchoolType(): String = dataStore.data.map { it[SCHOOL_TYPE] ?: "" }.first()
    suspend fun getGrade(): String = dataStore.data.map { it[USER_GRADE] ?: "1" }.first()
    suspend fun getClass(): String = dataStore.data.map { it[USER_CLASS] ?: "1" }.first()

    val schoolNameFlow: Flow<String> = dataStore.data
        .map { it[SCHOOL_NAME] ?: "" }
}