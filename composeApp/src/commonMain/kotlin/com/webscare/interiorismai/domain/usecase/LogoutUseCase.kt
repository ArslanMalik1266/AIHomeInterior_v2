package com.webscare.interiorismai.domain.usecase

import com.webscare.interiorismai.domain.model.LogoutDomainModel
import com.webscare.interiorismai.domain.repo.AuthRepository

class LogoutUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(
        packageName: String,
        userEmail: String,
        deviceId: String,

    ): Result<LogoutDomainModel> {
        return repository.logout(packageName, userEmail,deviceId )
    }
}