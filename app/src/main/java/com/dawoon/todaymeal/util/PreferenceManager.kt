package com.dawoon.todaymeal.util

import android.content.SharedPreferences
import javax.inject.Inject

class PreferenceManager @Inject constructor(private val prefs: SharedPreferences) {
    fun saveSchool(atpt: String, code: String, name: String) {
        prefs.edit().apply {
            putString("ATPT_CODE", atpt)
            putString("SCHOOL_CODE", code)
            putString("SCHOOL_NAME", name)
            apply()
        }
    }

    fun saveGradeAndClass(grade: String, classNum: String) {
        prefs.edit().apply {
            putString("USER_GRADE", grade)
            putString("USER_CLASS", classNum)
            apply()
        }
    }

    fun getSchoolName(): String = prefs.getString("SCHOOL_NAME", "") ?: ""
}