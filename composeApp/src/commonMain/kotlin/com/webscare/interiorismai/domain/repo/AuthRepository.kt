package com.webscare.interiorismai.domain.repo

import com.webscare.interiorismai.domain.model.DeviceLinkResult
import com.webscare.interiorismai.domain.model.LogoutDomainModel
import com.webscare.interiorismai.domain.model.VerifyResponse

interface AuthRepository {
    suspend fun login(
        packageName: String,
        deviceId: String,
        userEmail: String,
        authProvider: String
    ): Result<VerifyResponse>

//    suspend fun loginWithGoogle(
//        packageName: String,
//        deviceId: String,
//        userEmail: String,
//        authProvider: String
//    ): Result<VerifyResponse>
    suspend fun verifyOtp(
        packageName: String,
        deviceId: String,
        userEmail: String,
        authProvider: String,
        otp: String
    ): Result<DeviceLinkResult>

    suspend fun logout(
        packageName: String,
        userEmail: String,
        deviceId: String
    ): Result<LogoutDomainModel>

    suspend fun getProfileAndCredits(
        packageName: String,
        deviceId: String,
        userEmail: String,
        authProvider: String
    ): Result<DeviceLinkResult>
}