package com.webscare.interiorismai.data.remote.service

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Parameters
import com.webscare.interiorismai.domain.model.CreditResponse
import com.webscare.interiorismai.domain.model.SpendCreditsResponse

class AuthService(
    private val client: HttpClient,
    private val baseUrl: String,
    private val apiKey: String
) {


    // Login (OTP Send karne ke liye)
    suspend fun login(
        email: String,
        deviceId: String,
        authProvider: String,
    ): HttpResponse = client.submitForm(
        url = "$baseUrl/device/link",
        formParameters = Parameters.build {
            append("package_name", "com.webscare.interiorismai")
            append("device_id", deviceId)
            append("user_email", email)
            append("auth_provider", authProvider)
        }
    ) {
        header("X-API-KEY", apiKey)
    }

    // Verify OTP (Link karne ke liye)
    suspend fun verifyOtp(
        deviceId: String,
        userEmail: String,
        authProvider: String,
        otp: String
    ): HttpResponse = client.submitForm(
        url = "$baseUrl/device/link",
        formParameters = Parameters.build {
            append("package_name", "com.webscare.interiorismai")
            append("device_id", deviceId)
            append("user_email", userEmail)
            append("auth_provider", authProvider)
            append("otp", otp)
        }
    ) {
        header("X-API-KEY", apiKey)
    }

    suspend fun logout(
        packageName: String,
        email: String,
        deviceId: String
    ): HttpResponse = client.submitForm(
        url = "$baseUrl/auth/logout",
        formParameters = Parameters.build {
            append("package_name", "com.webscare.interiorismai") // Hardcoded
            append("user_email", email)
            append("device_id", deviceId)

        }
    ) {
        println("DEBUG_SERVICE_LOGOUT: URL -> $baseUrl/auth/logout")
        println("DEBUG_SERVICE_LOGOUT: Form Parameters -> package_name=$packageName, user_email=$email, device_id=$deviceId")
        println("DEBUG_SERVICE_LOGOUT: Headers -> X-API-KEY=$apiKey")
        header("X-API-KEY", apiKey)
    }

    suspend fun getProfileData(
        email: String,
        deviceId: String,
        authProvider: String,
    ): HttpResponse = client.submitForm(
        url = "$baseUrl/device/link",
        formParameters = Parameters.build {
            append("package_name", "com.webscare.interiorismai")
            append("device_id", deviceId)
            append("user_email", email)
            append("auth_provider", authProvider)
        }
    ) {
        header("X-API-KEY", apiKey)
    }

    suspend fun addCredits(email: String, amount: Int): CreditResponse {
        return client.submitForm(
            url = "$baseUrl/credits/add",
            formParameters = Parameters.build {
                append("user_email", email)
                append("amount", amount.toString())
                append("package_name", "com.webscare.interiorismai")
            }
        ) {
            header("X-API-KEY", apiKey)
            println("DEBUG_SERVICE_CREDITS: URL -> $baseUrl/credits/add")
            println("DEBUG_SERVICE_CREDITS: Email -> $email, Amount -> $amount")
        }.body()
    }
    suspend fun registerGuest(
        packageName: String,
        deviceId: String
    ): HttpResponse = client.submitForm(
        url = "$baseUrl/device/register",
        formParameters = Parameters.build {
            append("package_name", packageName)
            append("device_id", deviceId)
        }
    ) {
        header("X-API-KEY", apiKey)
    }

    suspend fun spendCredits(
        email: String,
        amount: Int,
        deviceId: String,
        packageName: String
    ): SpendCreditsResponse {
        return client.submitForm(
            url = "$baseUrl/credits/spend",
            formParameters = Parameters.build {
                append("user_email", email)
                append("amount", amount.toString())
                append("device_id", deviceId)
                append("package_name", packageName)
            }
        ) {
            header("X-API-KEY", apiKey)
        }.body()
    }
    suspend fun spendCreditsGuest(
        amount: Int,
        deviceId: String,
        packageName: String
    ): SpendCreditsResponse {
        return client.submitForm(
            url = "$baseUrl/credits/spend",
            formParameters = Parameters.build {
                append("amount", amount.toString())
                append("device_id", deviceId)
                append("package_name", packageName)
            }
        ) {
            header("X-API-KEY", apiKey)
        }.body()
    }

}
