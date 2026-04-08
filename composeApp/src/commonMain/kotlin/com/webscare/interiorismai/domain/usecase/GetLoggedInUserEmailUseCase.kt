package com.webscare.interiorismai.domain.usecase

import com.webscare.interiorismai.domain.model.DeviceLinkResult
import com.webscare.interiorismai.domain.repo.AuthRepository

class GetLoggedInUserUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): DeviceLinkResult? {

        return try {
            val result = authRepository.verifyOtp(
                packageName = "com.webscare.interiorismai",
                deviceId = "device-id-placeholder",
                userEmail = "user-email-placeholder",
                authProvider = "google",
                otp = "otp-placeholder"
            )
            if (result.isSuccess) result.getOrNull() else null
        } catch (e: Exception) {
            null
        }
    }
}