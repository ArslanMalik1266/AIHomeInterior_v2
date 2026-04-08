package com.webscare.interiorismai.domain.usecase

import com.webscare.interiorismai.domain.model.DeviceLinkResult
import com.webscare.interiorismai.domain.repo.AuthRepository

class VerifyOtpUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(
        packageName: String,
        deviceId: String,
        userEmail: String,
        authProvider: String,
        otp: String
    ): Result<DeviceLinkResult> {
        return repository.verifyOtp(packageName, deviceId, userEmail, authProvider, otp)
    }
}