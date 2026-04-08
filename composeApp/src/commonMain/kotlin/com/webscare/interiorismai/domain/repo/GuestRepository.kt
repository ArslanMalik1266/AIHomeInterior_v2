package com.webscare.interiorismai.domain.repo

import com.webscare.interiorismai.data.remote.util.ResultState
import com.webscare.interiorismai.domain.model.GuestSession

interface GuestRepository {
    suspend fun registerGuest(
        packageName: String,
        deviceId: String
    ): ResultState<GuestSession>
}