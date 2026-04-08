package com.webscare.interiorismai.domain.repo

import com.webscare.interiorismai.domain.model.CreditResponse
import com.webscare.interiorismai.domain.model.SpendCreditsResponse


interface CreditsRepository {
    suspend fun addCredits(email: String, amount: Int): Result<CreditResponse>
        suspend fun spendCredits(
            userEmail: String,
            amount: Int,
            deviceId: String,
            packageName: String
        ): Result<SpendCreditsResponse>
    suspend fun spendCreditsGuest(
        amount: Int,
        deviceId: String,
        packageName: String
    ): Result<SpendCreditsResponse>
    }
