package com.dawoon.todaymeal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dawoon.todaymeal.network.model.SchoolType
import com.dawoon.todaymeal.network.onError
import com.dawoon.todaymeal.network.onFailure
import com.dawoon.todaymeal.network.onSuccess
import com.dawoon.todaymeal.repository.TimetableRepository
import com.dawoon.todaymeal.repository.TimetableSubject
import com.dawoon.todaymeal.util.DateCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TimetableUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val infoMessage: String? = null // "데이터가 없습니다" 등의 안내용
)

@HiltViewModel
class TimetableViewModel @Inject constructor(
    private val repository: TimetableRepository
): ViewModel() {
    private val _timetableState = MutableStateFlow<List<TimetableSubject>>(emptyList())
    val timetableState = _timetableState.asStateFlow()
    private val _uiState = MutableStateFlow(TimetableUiState())
    val uiState = _uiState.asStateFlow()
    val maxPeriod = _timetableState.map { list ->
        val max = list.maxOfOrNull { it.period } ?: 7
        if (max < 7) 7 else max
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 7)

    fun fetchTimetable(from: String, to: String) {
        viewModelScope.launch {
            repository.getTimetable(
                schoolType = SchoolType.HIGH,
                atptCode = "J10",
                schoolCode = "7530528",
                grade = "1",
                classNm = "1",
                fromYmd = from,
                toYmd = to
            ).onSuccess { list ->
                _timetableState.value = list
                _uiState.value = TimetableUiState(isLoading = false)
            }.onFailure {
                _uiState.value = TimetableUiState(
                    isLoading = false,
                    errorMessage = "네트워크 오류가 발생했습니다.\n잠시 후 다시 시도해주세요."
                )
            }.onError { code, _ ->
                val userMsg = when (code) {
                    "INFO-200" -> "해당 기간에 등록된 시간표가 없습니다."
                    "ERROR-300" -> "인증 키가 유효하지 않습니다."
                    else -> "데이터를 가져오지 못했습니다. ($code)"
                }
                _uiState.value = TimetableUiState(
                    isLoading = false,
                    infoMessage = if (code == "INFO-200") userMsg else null,
                    errorMessage = if (code != "INFO-200") userMsg else null
                )
                _timetableState.value = emptyList()
            }
        }
    }
}