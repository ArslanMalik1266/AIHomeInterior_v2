package com.webscare.interiorismai.utils

import com.webscare.interiorismai.data.remote.util.ResultState

suspend fun <T> executeApiCall(
    updateState: (ResultState<T>) -> Unit,
    apiCall: suspend () -> T,
    onSuccess: suspend (T) -> Unit,
    onError: (String) -> Unit
) {
    updateState(ResultState.Loading)
    try {
        val response = apiCall()
        updateState(ResultState.Success(response))
        onSuccess(response)
    } catch (e: Exception) {
        val errorMessage = e.message ?: "Operation failed. Please try again."
        updateState(ResultState.Failure(errorMessage))
        onError(errorMessage)
    }
}