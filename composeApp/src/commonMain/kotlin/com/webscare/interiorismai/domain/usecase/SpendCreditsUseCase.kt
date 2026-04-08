package com.webscare.interiorismai.domain.usecase

import com.webscare.interiorismai.domain.model.SpendCreditsResponse
import com.webscare.interiorismai.domain.repo.CreditsRepository

class SpendCreditsUseCase(
    private val creditsRepository: CreditsRepository
) {
    suspend operator fun invoke(
        userEmail: String,
        deviceId: String,
        amount: Int = 1,
        packageName: String = "com.webscare.interiorismai"
    ): Result<SpendCreditsResponse> {
        return creditsRepository.spendCredits(
            userEmail = userEmail,
            amount = amount,
            deviceId = deviceId,
            packageName = packageName
        )
    }
}

class SpendCreditsUseCaseGuest(
    private val creditsRepository: CreditsRepository
) {
    suspend operator fun invoke(
        deviceId: String,
        amount: Int = 1,
        packageName: String = "com.webscare.interiorismai"
    ): Result<SpendCreditsResponse> {
        return creditsRepository.spendCreditsGuest(
            amount = amount,
            deviceId = deviceId,
            packageName = packageName
        )
    }
}