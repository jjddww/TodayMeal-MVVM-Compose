package com.dawoon.todaymeal.repository

import com.dawoon.todaymeal.network.ApiResult
import com.dawoon.todaymeal.network.Apis
import com.dawoon.todaymeal.network.mapSuccess
import com.dawoon.todaymeal.network.model.NeisResultDto
import com.dawoon.todaymeal.network.model.SchoolScheduleResponseDto
import com.dawoon.todaymeal.network.model.SchoolScheduleRowDto
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Named


interface ScheduleRepository {
    suspend fun getSchoolSchedule(
        atptCode: String,
        schoolCode: String,
        fromYmd: String? = null,
        toYmd: String? = null,
        ay: String? = null
    ): ApiResult<List<SchoolScheduleRowDto>>
}

class ScheduleRepositoryImpl @Inject constructor(
    private val api: Apis,
    @Named("NEIS_API_KEY") private val apiKey: String
) : ScheduleRepository {

    private suspend fun <T> safeCall(
        call: suspend () -> Response<T>,
        resultExtractor: (T) -> NeisResultDto?
    ): ApiResult<T> {
        return try {
            val res = call()
            if (!res.isSuccessful) {
                return ApiResult.Failure(RuntimeException("HTTP ${res.code()} ${res.message()}"))
            }
            val body = res.body()
                ?: return ApiResult.Failure(NullPointerException("Response body is null"))

            val result = resultExtractor(body)
            val code = result?.CODE
            val msg = result?.MESSAGE

            if (code != null && code != "INFO-000") {
                ApiResult.Error(code, msg)
            } else {
                ApiResult.Success(body)
            }
        } catch (t: Throwable) {
            ApiResult.Failure(t)
        }
    }

    override suspend fun getSchoolSchedule(
        atptCode: String,
        schoolCode: String,
        fromYmd: String?,
        toYmd: String?,
        ay: String?
    ): ApiResult<List<SchoolScheduleRowDto>> {

        return safeCall(
            call = { api.getSchoolSchedule(
                key = apiKey,
                atptCode = atptCode,
                schoolCode = schoolCode,
                fromYmd = fromYmd,
                toYmd = toYmd,
                ay = ay
            ) },
            resultExtractor = { body: SchoolScheduleResponseDto ->
                body.SchoolSchedule
                    ?.firstOrNull()
                    ?.head
                    ?.firstOrNull { it.RESULT != null }
                    ?.RESULT
            }
        ).mapSuccess { dto: SchoolScheduleResponseDto ->
            dto.SchoolSchedule?.getOrNull(1)?.row.orEmpty()
        }
    }
}