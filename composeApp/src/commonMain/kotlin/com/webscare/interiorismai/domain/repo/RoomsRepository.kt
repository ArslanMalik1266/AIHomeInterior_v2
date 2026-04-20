package com.webscare.interiorismai.domain.repo

import com.webscare.interiorismai.domain.model.RoomUi
import com.webscare.interiorismai.domain.model.Rooms
import kotlinx.coroutines.flow.Flow

interface RoomsRepository {
    fun getRoomsFlow(): Flow<List<RoomUi>>
    suspend fun getRoomsList(): Rooms
    suspend fun refreshRooms()

}
