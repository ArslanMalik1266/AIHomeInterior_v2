package com.webscare.interiorismai.utils

actual class GoogleSignInHelper {
    actual suspend fun signIn(): GoogleSignInResult {
        // iOS implementation baad mein
        return GoogleSignInResult(error = "iOS not implemented yet")
    }
}