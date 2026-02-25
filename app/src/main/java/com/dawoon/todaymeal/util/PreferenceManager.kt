package com.dawoon.todaymeal.util

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface PreferenceManager {
    suspend fun saveSchool(atpt: String, code: String, name: String)
    suspend fun saveGradeAndClass(grade: String, classNum: String)
    suspend fun clearAll()
    suspend fun getAtptCode(): String
    suspend fun getSchoolCode(): String
    suspend fun getSchoolName(): String
    suspend fun getSchoolType(): String
    suspend fun getGrade(): String
    suspend fun getClass(): String
    val schoolNameFlow: Flow<String>
}


class PreferenceManagerImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : PreferenceManager {

    companion object {
        private val ATPT_CODE = stringPreferencesKey("WIDGET_ATPT_CODE")
        private val SCHOOL_CODE = stringPreferencesKey("WIDGET_SCHOOL_CODE")
        private val SCHOOL_NAME = stringPreferencesKey("SCHOOL_NAME")
        private val SCHOOL_TYPE = stringPreferencesKey("SCHOOL_TYPE")
        private val USER_GRADE = stringPreferencesKey("USER_GRADE")
        private val USER_CLASS = stringPreferencesKey("USER_CLASS")
    }

    override suspend fun saveSchool(atpt: String, code: String, name: String) {
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

    override suspend fun saveGradeAndClass(grade: String, classNum: String) {
        dataStore.edit { prefs ->
            prefs[USER_GRADE] = grade
            prefs[USER_CLASS] = classNum
        }
    }

    override suspend fun clearAll() {
        dataStore.edit { it.clear() }
    }

    override suspend fun getAtptCode(): String = dataStore.data.map { it[ATPT_CODE] ?: "" }.first()
    override suspend fun getSchoolCode(): String = dataStore.data.map { it[SCHOOL_CODE] ?: "" }.first()
    override suspend fun getSchoolName(): String = dataStore.data.map { it[SCHOOL_NAME] ?: "" }.first()
    override suspend fun getSchoolType(): String = dataStore.data.map { it[SCHOOL_TYPE] ?: "" }.first()
    override suspend fun getGrade(): String = dataStore.data.map { it[USER_GRADE] ?: "1" }.first()
    override suspend fun getClass(): String = dataStore.data.map { it[USER_CLASS] ?: "1" }.first()

    override val schoolNameFlow: Flow<String> = dataStore.data.map { it[SCHOOL_NAME] ?: "" }
}