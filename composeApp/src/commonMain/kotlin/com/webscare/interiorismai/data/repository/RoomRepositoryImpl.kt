    package com.webscare.interiorismai.data.repository

    import com.webscare.interiorismai.data.local.dao.RoomDao
    import com.webscare.interiorismai.data.mapper.toEntity
    import com.webscare.interiorismai.data.mapper.toUi
    import io.ktor.client.call.body
    import com.webscare.interiorismai.data.remote.service.RoomService
    import kotlinx.coroutines.flow.map
    import com.webscare.interiorismai.domain.model.RoomUi
    import com.webscare.interiorismai.domain.model.Rooms
    import com.webscare.interiorismai.domain.repo.RoomsRepository
    import kotlinx.coroutines.flow.Flow

    class RoomRepositoryImpl(val roomService: RoomService,private val roomDao: RoomDao) : RoomsRepository {

        override fun getRoomsFlow(): Flow<List<RoomUi>> {
            return roomDao.getAllRooms().map { entities ->
                println("CACHE_DEBUG Reading from Cache: ${entities.size} items")

                entities.map { it.toUi() }
            }
        }

        override suspend fun getRoomsList(): Rooms =
            roomService.getRooms().body<Rooms>()

        override suspend fun refreshRooms() {
            try {
                // 1. API Call
                val response = roomService.getRooms().body<Rooms>()

                // 2. Agar API successful hai, toh DB update karein
                if (response.success) {
                    val entities = response.rooms.map { it.toEntity()}
                    println("CACHE_DEBUG Data saved to DB: ${entities.size} items")

                    roomDao.insertAll(entities)
                }
            } catch (e: Exception) {
                // Agar internet nahi hai ya error aata hai, toh DB ka purana data
                // automatically UI pe dikhta rahega kyunki UI Flow observe kar raha hai.
                e.printStackTrace()
            }
        }


    }
