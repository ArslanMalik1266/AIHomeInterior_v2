package com.webscare.interiorismai.data.repository

import io.ktor.client.call.body
import com.webscare.interiorismai.data.mapper.toDomain
import com.webscare.interiorismai.data.remote.dto.GuestSessionDto
import com.webscare.interiorismai.data.remote.service.AuthService
import com.webscare.interiorismai.data.remote.util.ResultState
import com.webscare.interiorismai.domain.model.GuestSession
import com.webscare.interiorismai.domain.repo.GuestRepository

class GuestRepositoryImpl(
    private val authService: AuthService
) : GuestRepository {

    override suspend fun registerGuest(
        packageName: String,
        deviceId: String
    ): ResultState<GuestSession> {
        return try {
            val response = authService.registerGuest(
                packageName = packageName,
                deviceId = deviceId
            ).body<GuestSessionDto>()
            ResultState.Success(response.toDomain())
        } catch (e: Exception) {
            println("DEBUG_GUEST_REPO: Exception = ${e.message}")
            ResultState.Error(e.message ?: "Unknown error")
        }
    }
}