package com.webscare.interiorismai.utils

expect class GoogleSignInHelper {
    suspend fun signIn(): GoogleSignInResult
}

data class GoogleSignInResult(
    val email: String? = null,
    val error: String? = null
) {
    val isSuccess: Boolean get() = email != null && error == null
}