package com.dawoon.todaymeal.util

import android.content.SharedPreferences
import javax.inject.Inject

class PreferenceManager @Inject constructor(private val prefs: SharedPreferences) {
    fun saveSchool(atpt: String, code: String, name: String) {
        prefs.edit().apply {
            putString("ATPT_CODE", atpt)
            putString("SCHOOL_CODE", code)
            putString("SCHOOL_NAME", name)

            if (name.contains("초등학교")) {
                putString("SCHOOL_TYPE", "ELEMENTARY")
            } else if (name.contains("중학교")) {
                putString("SCHOOL_TYPE", "MIDDLE")
            } else if (name.contains("고등학교")) {
                putString("SCHOOL_TYPE", "HIGH")
            } else {
                putString("SCHOOL_TYPE" ,"")
            }
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

    fun clearAll() {
        prefs.edit().clear().apply()
    }

    fun getAtptCode(): String = prefs.getString("ATPT_CODE", "") ?: ""

    fun getSchoolCode(): String = prefs.getString("SCHOOL_CODE", "") ?: ""
    fun getSchoolName(): String = prefs.getString("SCHOOL_NAME", "") ?: ""

    fun getSchoolType(): String = prefs.getString("SCHOOL_TYPE", "") ?: ""

    fun getGrade(): String = prefs.getString("USER_GRADE", "1") ?: "1"

    fun getClass(): String = prefs.getString("USER_CLASS", "1") ?: "1"
}