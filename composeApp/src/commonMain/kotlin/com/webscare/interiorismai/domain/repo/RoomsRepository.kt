package com.webscare.interiorismai.domain.repo

import com.webscare.interiorismai.domain.model.Rooms

interface RoomsRepository {
    suspend fun getRoomsList(): Rooms


}
