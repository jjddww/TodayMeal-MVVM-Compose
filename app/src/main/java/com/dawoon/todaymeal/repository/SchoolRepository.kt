package com.dawoon.todaymeal.repository

import com.dawoon.todaymeal.network.ApiResult
import com.dawoon.todaymeal.network.Apis
import com.dawoon.todaymeal.network.mapSuccess
import com.dawoon.todaymeal.network.model.MealRowDto
import com.dawoon.todaymeal.network.model.MealServiceDietInfoResponseDto
import com.dawoon.todaymeal.network.model.NeisResultDto
import com.dawoon.todaymeal.network.model.SchoolInfoResponseDto
import com.dawoon.todaymeal.network.model.SchoolRowDto
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Named


class SchoolRepository @Inject constructor(
    private val api: Apis,
    @Named("NEIS_API_KEY") private val apiKey: String
) {

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

//    suspend fun searchSchool(schoolName: String): ApiResult<List<SchoolRowDto>> {
//        return safeCall(
//            call = { api.getSchoolInfo(key = apiKey, schoolName = schoolName) },
//            resultExtractor = { body: SchoolInfoResponseDto ->
//                body.schoolInfo
//                    ?.firstOrNull()
//                    ?.head
//                    ?.firstOrNull { it.RESULT != null }
//                    ?.RESULT
//            }
//        ).mapSuccess { dto: SchoolInfoResponseDto ->
//            dto.schoolInfo?.getOrNull(1)?.row.orEmpty()
//        }
//    }

    suspend fun getMealServiceInfo(atptCode: String,
                                   schoolCode: String,
                                   mealCode: String,
                                   fromYmd: String?,
                                   toYmd: String?): ApiResult<List<MealRowDto>> {
        return safeCall(
            call = { api.getMeal(
                key = apiKey,
                atptCode = atptCode,
                schoolCode = schoolCode,
                mealCode = mealCode,
                fromYmd = fromYmd,
                toYmd = toYmd
            )},
            resultExtractor = { body: MealServiceDietInfoResponseDto ->
                body.mealServiceDietInfo
                    ?.firstOrNull()
                    ?.head
                    ?.firstOrNull() { it.RESULT != null }
                    ?.RESULT
            }
        ).mapSuccess { dto: MealServiceDietInfoResponseDto ->
            dto.mealServiceDietInfo?.getOrNull(1)?.row.orEmpty()
        }

    }
}