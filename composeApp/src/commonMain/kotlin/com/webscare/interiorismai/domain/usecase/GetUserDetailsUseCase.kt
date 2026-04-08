package com.webscare.interiorismai.domain.usecase

import com.webscare.interiorismai.domain.model.DeviceLinkResult
import com.webscare.interiorismai.domain.repo.AuthRepository

class GetUserProfileUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(
        packageName: String,
        deviceId: String,
        userEmail: String,
        authProvider: String
    ): Result<DeviceLinkResult> {
        return repository.getProfileAndCredits(
            packageName = packageName,
            deviceId = deviceId,
            userEmail = userEmail,
            authProvider = authProvider
        )
    }
}