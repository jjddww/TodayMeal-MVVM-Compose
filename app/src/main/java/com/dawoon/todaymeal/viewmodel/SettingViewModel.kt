package com.dawoon.todaymeal.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val prefManager: PreferenceManager
) : ViewModel() {

    var searchQuery by mutableStateOf("")
    var schoolList = mutableStateOf<List<SchoolRowDto>>(emptyList())
    var isLoading by mutableStateOf(false)
    var showPopup by mutableStateOf(false)
    var showGradePopup by mutableStateOf(false)
    var inputGrade by mutableStateOf("")
    var inputClass by mutableStateOf("")

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
                is ApiResult.Error -> { /* 에러 처리 */ }
                is ApiResult.Failure -> { /* 실패 처리 */ }
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

        if (inputGrade.isNotBlank() && inputClass.isNotBlank()) {
            prefManager.saveSchool(
                atpt = school.ATPT_OFCDC_SC_CODE ?: "",
                code = school.SD_SCHUL_CODE ?: "",
                name = school.SCHUL_NM ?: ""
            )
            prefManager.saveGradeAndClass(inputGrade, inputClass)

            showGradePopup = false
            onComplete()
        }
    }
}