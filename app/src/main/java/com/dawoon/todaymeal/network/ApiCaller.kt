package com.dawoon.todaymeal.network

import com.dawoon.todaymeal.network.model.NeisResultDto
import retrofit2.Response

object NeisApiCaller {
    suspend fun <T> call(
        apiCall: suspend () -> Response<T>,
        resultExtractor: (T) -> NeisResultDto?
    ): ApiResult<T> {
        return try {
            val res = apiCall()
            if (!res.isSuccessful) {
                return ApiResult.Failure(RuntimeException("HTTP ${res.code()}"))
            }
            val body = res.body() ?: return ApiResult.Failure(NullPointerException())

            val result = resultExtractor(body)
            if (result?.CODE != null && result.CODE != "INFO-000") {
                ApiResult.Error(result.CODE, result.MESSAGE)
            } else {
                ApiResult.Success(body)
            }
        } catch (t: Throwable) {
            ApiResult.Failure(t)
        }
    }
}
