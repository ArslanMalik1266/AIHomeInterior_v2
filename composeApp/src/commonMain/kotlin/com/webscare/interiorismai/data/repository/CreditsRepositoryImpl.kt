package com.webscare.interiorismai.data.repository

import com.webscare.interiorismai.data.remote.service.AuthService
import com.webscare.interiorismai.domain.model.CreditResponse
import com.webscare.interiorismai.domain.model.SpendCreditsResponse
import com.webscare.interiorismai.domain.repo.CreditsRepository

class CreditsRepositoryImpl(
    private val authService: AuthService
) : CreditsRepository {

    override suspend fun addCredits(email: String, amount: Int): Result<CreditResponse> {
        println("DEBUG_REPO: Calling API with Email: $email, Amount: $amount")
        return try {
            println("DEBUG_REPO: 2. Calling roomService.addCredits...") // <--- Yahan
            val response = authService.addCredits(email, amount)
            println("DEBUG_REPO: Raw Response Check: $response")
            if (response.status == "added") {
                println("DEBUG_REPO: 4. Success Condition Met") // <--- Yahan
                Result.success(response)
            } else {
                println("DEBUG_REPO: 5. Failed Condition: Status was ${response.status}") // <--- Yahan
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            println("DEBUG_REPO: 6. Exception Caught: ${e.message}")
            Result.failure(e)
        }
    }
    override suspend fun spendCredits(
        userEmail: String,
        amount: Int,
        deviceId: String,
        packageName: String
    ): Result<SpendCreditsResponse> {
        return try {
            val response = authService.spendCredits(
                email = userEmail,
                amount = amount,
                deviceId = deviceId,
                packageName = packageName
            )
            println("🔴 SPEND_RESPONSE: status=${response.status}, total=${response.total_credits}")
            println("🔴 SPEND_REQUEST: email=$userEmail, deviceId=$deviceId, amount=$amount, package=$packageName")

            if (response.status == "success")  {
                Result.success(response)
            } else {
                Result.failure(Exception("Not enough credits"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun spendCreditsGuest(
        amount: Int,
        deviceId: String,
        packageName: String
    ): Result<SpendCreditsResponse> {
        return try {
            val response = authService.spendCreditsGuest(
                amount = amount,
                deviceId = deviceId,
                packageName = packageName
            )
            println("🔴 SPEND_RESPONSE: status=${response.status}, total=${response.total_credits}")

            if (response.status == "success" )  {
                Result.success(response)
            } else {
                Result.failure(Exception("Not enough credits"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}