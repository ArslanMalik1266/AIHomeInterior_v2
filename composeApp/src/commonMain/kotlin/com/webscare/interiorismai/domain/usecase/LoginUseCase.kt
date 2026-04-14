package com.webscare.interiorismai.domain.usecase

import com.webscare.interiorismai.domain.model.VerifyResponse
import com.webscare.interiorismai.domain.repo.AuthRepository

class LoginUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(
        packageName: String,
        deviceId: String,
        userEmail: String,
        authProvider: String = "email"
    ): Result<VerifyResponse> {
        return repository.login(packageName, deviceId, userEmail, authProvider)
    }
}

//class LoginWithGoogleUseCase(private val repository: AuthRepository) {
//    suspend operator fun invoke(
//        packageName: String,
//        deviceId: String,
//        userEmail: String,
//        authProvider: String = "google"
//    ): Result<VerifyResponse> {
//        return repository.loginWithGoogle(packageName, deviceId, userEmail, authProvider)
//    }
//}