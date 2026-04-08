package com.webscare.interiorismai.data.repository

import io.ktor.client.call.body
import com.webscare.interiorismai.data.remote.service.RoomService
import com.webscare.interiorismai.domain.model.InteriorStyles
import com.webscare.interiorismai.domain.model.InteriorTypes
import com.webscare.interiorismai.domain.model.Rooms
import com.webscare.interiorismai.domain.repo.RoomsRepository

class RoomRepositoryImpl(val roomService: RoomService) : RoomsRepository {
    override suspend fun getRoomsList(): Rooms =
        roomService.getRooms().body<Rooms>()
}