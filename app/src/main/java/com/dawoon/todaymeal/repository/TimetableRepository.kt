package com.dawoon.todaymeal.repository

import com.dawoon.todaymeal.network.ApiResult
import com.dawoon.todaymeal.network.Apis
import com.dawoon.todaymeal.network.mapSuccess
import com.dawoon.todaymeal.network.model.HisTimetableRowDto
import com.dawoon.todaymeal.network.model.NeisResultDto
import com.dawoon.todaymeal.network.model.SchoolType
import com.dawoon.todaymeal.util.DateCalculator
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Named

data class TimetableSubject(
    val subject: String,
    val period: Int,
    val dayOfWeek: Int
)

class TimetableRepository @Inject constructor(
    private val api: Apis,
    @Named("NEIS_API_KEY") private val apiKey: String
) {
    // 공통 safeCall 로직 (기존과 동일하게 유지)
    private suspend fun <T> safeCall(
        call: suspend () -> Response<T>,
        resultExtractor: (T) -> NeisResultDto?
    ): ApiResult<T> {
        return try {
            val res = call()
            if (!res.isSuccessful) return ApiResult.Failure(RuntimeException("HTTP ${res.code()}"))
            val body = res.body() ?: return ApiResult.Failure(NullPointerException("Body null"))

            val result = resultExtractor(body)
            if (result?.CODE != null && result.CODE != "INFO-000") {
                ApiResult.Error(result.CODE, result.MESSAGE)
            } else {
                ApiResult.Success(body)
            }
        } catch (t: Throwable) { ApiResult.Failure(t) }
    }

    // 고등학교 시간표 가져오기 예시 (초/중도 동일한 구조로 추가 가능)
    suspend fun getTimetable(
        schoolType: SchoolType, // "ELEMENTARY", "MIDDLE", "HIGH"
        atptCode: String,
        schoolCode: String,
        grade: String,
        classNm: String,
        fromYmd: String,
        toYmd: String
    ): ApiResult<List<TimetableSubject>> {

        return when (schoolType) {
            SchoolType.ELEMENTARY -> {
                safeCall(
                    call = { api.getElsTimetable(apiKey, "json", atptCode, schoolCode, grade, classNm, fromYmd, toYmd) },
                    resultExtractor = { it.elsTimetable?.firstOrNull()?.head?.firstOrNull { h -> h.RESULT != null }?.RESULT }
                ).mapSuccess { body ->
                    body.elsTimetable?.getOrNull(1)?.row?.map {
                        TimetableSubject(it.ITRT_CNTNT ?: "", it.PERIO?.toIntOrNull() ?: 0, DateCalculator.getDayOfWeek(it.ALL_TI_YMD ?: ""))
                    } ?: emptyList()
                }
            }
            SchoolType.MIDDLE -> {
                safeCall(
                    call = { api.getMisTimetable(apiKey, "json", atptCode, schoolCode, grade, classNm, fromYmd, toYmd) },
                    resultExtractor = { it.misTimetable?.firstOrNull()?.head?.firstOrNull { h -> h.RESULT != null }?.RESULT }
                ).mapSuccess { body ->
                    body.misTimetable?.getOrNull(1)?.row?.map {
                        TimetableSubject(it.ITRT_CNTNT ?: "", it.PERIO?.toIntOrNull() ?: 0, DateCalculator.getDayOfWeek(it.ALL_TI_YMD ?: ""))
                    } ?: emptyList()
                }
            }
            else -> { // HIGH
                safeCall(
                    call = { api.getHisTimetable(apiKey, "json", atptCode, schoolCode, grade, classNm, fromYmd, toYmd) },
                    resultExtractor = { it.hisTimetable?.firstOrNull()?.head?.firstOrNull { h -> h.RESULT != null }?.RESULT }
                ).mapSuccess { body ->
                    body.hisTimetable?.getOrNull(1)?.row?.map {
                        TimetableSubject(it.ITRT_CNTNT ?: "", it.PERIO?.toIntOrNull() ?: 0, DateCalculator.getDayOfWeek(it.ALL_TI_YMD ?: ""))
                    } ?: emptyList()
                }
            }
        }
    }
}