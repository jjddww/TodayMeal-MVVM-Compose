package com.dawoon.todaymeal.network
sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Failure(val throwable: Throwable) : ApiResult<Nothing>() // 통신/예외
    data class Error(val code: String?, val message: String?) : ApiResult<Nothing>() // 서버 결과 코드
}

inline fun <T> ApiResult<T>.onSuccess(block: (T) -> Unit): ApiResult<T> {
    if (this is ApiResult.Success) block(data)
    return this
}

inline fun <T> ApiResult<T>.onFailure(block: (Throwable) -> Unit): ApiResult<T> {
    if (this is ApiResult.Failure) block(throwable)
    return this
}

inline fun <T> ApiResult<T>.onError(block: (String?, String?) -> Unit): ApiResult<T> {
    if (this is ApiResult.Error) block(code, message)
    return this
}

inline fun <T, R> ApiResult<T>.mapSuccess(
    mapper: (T) -> R
): ApiResult<R> =
    when (this) {
        is ApiResult.Success -> ApiResult.Success(mapper(data))
        is ApiResult.Failure -> this
        is ApiResult.Error -> this
    }
