package com.webscare.interiorismai.data.remote.util


sealed class ResultState<out T> {
    data class Success<out R>(val data: R) : ResultState<R>()
    data class Failure(val msg: String) : ResultState<Nothing>()
    data object Loading : ResultState<Nothing>()
    data class Error(val msg: String) : ResultState<Nothing>()
    data object Stable : ResultState<Nothing>()
}
