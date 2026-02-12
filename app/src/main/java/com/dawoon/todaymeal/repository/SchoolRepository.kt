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

    private val mealCache = mutableMapOf<String, List<MealRowDto>>()
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


    suspend fun getMealServiceInfo(
        atptCode: String,
        schoolCode: String,
        mealCode: String,
        fromYmd: String?,
        toYmd: String?
    ): ApiResult<List<MealRowDto>> {

        val cacheKey = "$mealCode-$fromYmd-$toYmd"

        if (mealCache.containsKey(cacheKey)) {
            return ApiResult.Success(mealCache[cacheKey]!!)
        }

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
                    ?.firstOrNull { it.RESULT != null }
                    ?.RESULT
            }
        ).mapSuccess { dto: MealServiceDietInfoResponseDto ->
            val rows = dto.mealServiceDietInfo?.getOrNull(1)?.row.orEmpty()

            if (rows.isNotEmpty()) {
                mealCache[cacheKey] = rows
            }
            rows
        }
    }

    fun clearCache() {
        mealCache.clear()
    }
}