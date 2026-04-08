package com.webscare.interiorismai.domain.usecase

import com.webscare.interiorismai.domain.model.CreditResponse
import com.webscare.interiorismai.domain.repo.CreditsRepository

class AddCreditsUseCase(private val repository: CreditsRepository) {

    suspend operator fun invoke(email: String, amount: Int): Result<CreditResponse> {
        if (email.isBlank()) {
            return Result.failure(Exception("Email cannot be empty"))
        }
        return repository.addCredits(email, amount)
    }
}