package com.webscare.interiorismai.domain.usecase

import com.webscare.interiorismai.data.remote.util.ResultState
import com.webscare.interiorismai.domain.model.GuestSession
import com.webscare.interiorismai.domain.repo.GuestRepository

class RegisterGuestUseCase(
    private val repository: GuestRepository
) {
    suspend operator fun invoke(
        packageName: String,
        deviceId: String
    ): ResultState<GuestSession> {
        return repository.registerGuest(packageName, deviceId)
    }
}