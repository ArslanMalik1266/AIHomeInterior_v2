package com.webscare.interiorismai.data.repository

import io.ktor.client.call.body
import io.ktor.http.HttpStatusCode
import com.webscare.interiorismai.data.remote.dto.DeviceLinkResponseDto
import com.webscare.interiorismai.data.remote.service.AuthService
import com.webscare.interiorismai.domain.model.DeviceLinkResult
import com.webscare.interiorismai.domain.model.VerifyResponse
import com.webscare.interiorismai.domain.repo.AuthRepository
import com.webscare.interiorismai.data.mapper.toDomain
import com.webscare.interiorismai.data.remote.dto.LogoutResponseDto
import com.webscare.interiorismai.domain.model.LogoutDomainModel

class AuthRepositoryImpl(
    private val authService: AuthService,
) : AuthRepository {

    override suspend fun verifyOtp(
        packageName: String,
        deviceId: String,
        userEmail: String,
        authProvider: String,
        otp: String
    ): Result<DeviceLinkResult> {
        return try {
            val response = authService.verifyOtp(
                deviceId = deviceId,
                userEmail = userEmail,
                authProvider = authProvider,
                otp = otp
            )

            if (response.status == HttpStatusCode.OK) {
                val dto = response.body<DeviceLinkResponseDto>()
                Result.success(dto.toDomain())
            } else {
                Result.failure(Exception("Failed with status: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun login(
        packageName: String,
        deviceId: String,
        userEmail: String,
        authProvider: String
    ): Result<VerifyResponse> {
        return runCatching {
            authService.login(userEmail, deviceId, authProvider).body<VerifyResponse>()
        }.onFailure { e ->
            println("DEBUG: Repository Error: ${e.message}")
            e.printStackTrace()
        }
    }

    override suspend fun logout(
        packageName: String,
        userEmail: String,
        deviceId: String

    ): Result<LogoutDomainModel> = runCatching {
        val response = authService.logout(packageName, userEmail, deviceId)

        if (response.status == HttpStatusCode.OK) {
            // DTO ko Domain model mein map karke bhej rahe hain
            response.body<LogoutResponseDto>().toDomain()
        } else {
            throw Exception("Logout failed: ${response.status}")
        }
    }.onFailure { e ->
        println("DEBUG: Repository Logout Error: ${e.message}")
    }

    override suspend fun getProfileAndCredits(
        packageName: String,
        deviceId: String,
        userEmail: String,
        authProvider: String
    ): Result<DeviceLinkResult> {
        return runCatching {
            val response = authService.getProfileData(
                email = userEmail,
                deviceId = deviceId,
                authProvider = authProvider
            )

            if (response.status == HttpStatusCode.OK) {
                val dto = response.body<DeviceLinkResponseDto>()
                dto.toDomain()
            } else {
                throw Exception("Failed to fetch profile: ${response.status}")
            }
        }.onFailure { e ->
            println("DEBUG: Profile Fetch Error: ${e.message}")
            e.printStackTrace()
        }
    }

    }