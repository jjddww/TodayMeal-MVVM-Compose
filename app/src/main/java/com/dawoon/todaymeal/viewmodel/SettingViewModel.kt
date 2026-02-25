package com.dawoon.todaymeal.viewmodel

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dawoon.todaymeal.AppWidget
import com.dawoon.todaymeal.network.ApiResult
import com.dawoon.todaymeal.network.model.SchoolRowDto
import com.dawoon.todaymeal.repository.SettingRepository
import com.dawoon.todaymeal.util.PreferenceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val repository: SettingRepository,
    private val prefManager: PreferenceManager,
) : ViewModel() {

    var searchQuery by mutableStateOf("")
    var schoolList = mutableStateOf<List<SchoolRowDto>>(emptyList())
    var isLoading by mutableStateOf(false)
    var showPopup by mutableStateOf(false)
    var showGradePopup by mutableStateOf(false)
    var inputGrade by mutableStateOf("")
    var inputClass by mutableStateOf("")

    var errorMessage by mutableStateOf("")

    private var tempSchool: SchoolRowDto? = null


    fun searchSchool() {
        if (searchQuery.isBlank()) return

        viewModelScope.launch {
            isLoading = true
            when (val result = repository.searchSchool(searchQuery)) {
                is ApiResult.Success -> {
                    schoolList.value = result.data
                    showPopup = true
                }
                is ApiResult.Error -> {
                    errorMessage = "서버 오류가 발생했습니다. (코드: ${result.code})"
                }
                is ApiResult.Failure -> {
                    errorMessage = "네트워크 연결을 확인해주세요."
                }
            }
            isLoading = false
        }
    }

    fun onSchoolSelected(school: SchoolRowDto) {
        tempSchool = school
        showPopup = false
        showGradePopup = true
    }


    fun saveFinalSettings(onComplete: () -> Unit) {
        val school = tempSchool ?: return

        viewModelScope.launch {
            if (inputGrade.isNotBlank() && inputClass.isNotBlank()) {
                prefManager.saveSchool(
                    atpt = school.ATPT_OFCDC_SC_CODE ?: "",
                    code = school.SD_SCHUL_CODE ?: "",
                    name = school.SCHUL_NM ?: ""
                )
                prefManager.saveGradeAndClass(inputGrade, inputClass)

                showGradePopup = false
                onComplete() // 저장이 끝났으니 UI에 알려줌
            }
        }
    }
}