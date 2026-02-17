package com.dawoon.todaymeal.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dawoon.todaymeal.network.model.SchoolScheduleRowDto
import com.dawoon.todaymeal.network.onError
import com.dawoon.todaymeal.network.onFailure
import com.dawoon.todaymeal.network.onSuccess
import com.dawoon.todaymeal.repository.ScheduleRepository
import com.dawoon.todaymeal.util.DateCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

data class ScheduleUiState(
    val loading: Boolean = false,
    val items: List<SchoolScheduleRowDto> = emptyList(),
    val errorMessage: String? = null
)

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val repository: ScheduleRepository
) : ViewModel() {

    private var currentBaseDate by mutableStateOf(Date())

//    private var currentBaseDate by mutableStateOf(DateCalculator.parseApiDate("20250514") ?: Date())
    val displayMonth get() = DateCalculator.formatMonth(currentBaseDate)
    private val _state = MutableStateFlow(ScheduleUiState())
    val state = _state.asStateFlow()

    private val errorData = listOf(
        SchoolScheduleRowDto(EVENT_NM = "문제가 \n발생하였습니다.\n잠시 후 \n다시 시도해주세요")
    )


    init {
        fetchScheduleData()
    }

    fun moveToPreviousMonth() {
        currentBaseDate = DateCalculator.getRelativeMonth(currentBaseDate, -1)
        fetchScheduleData()
    }

    fun moveToNextMonth() {
        currentBaseDate = DateCalculator.getRelativeMonth(currentBaseDate, 1)
        fetchScheduleData()
    }

    private fun fetchScheduleData() {
        val range = DateCalculator.getMonthRange(currentBaseDate)

        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true)
            repository.getSchoolSchedule(
                atptCode = "J10",
                schoolCode = "7530528",
                fromYmd = range.first,
                toYmd = range.second
            ).onSuccess { list ->
                _state.value = ScheduleUiState(items = list, loading = false)
            }.onFailure {
                _state.value = ScheduleUiState(loading = false, errorMessage = it.message)
            }.onError { code, msg ->
                _state.value = ScheduleUiState(
                    loading = false,
                    items = errorData,
                    errorMessage = "API ERROR $code: $msg"
                )
            }
        }
    }
}