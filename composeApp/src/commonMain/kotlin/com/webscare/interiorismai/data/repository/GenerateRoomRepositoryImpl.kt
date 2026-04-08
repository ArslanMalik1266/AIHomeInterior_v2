package com.webscare.interiorismai.data.repository

import com.webscare.interiorismai.data.remote.service.RoomService
import com.webscare.interiorismai.data.mapper.toDomain
import com.webscare.interiorismai.data.remote.util.ResultState
import com.webscare.interiorismai.domain.model.GenerateRoomRequest
import com.webscare.interiorismai.domain.model.GenerateRoomResult
import com.webscare.interiorismai.domain.repo.GenerateRoomRepository

class GenerateRoomRepositoryImpl(
    private val roomService: RoomService
) : GenerateRoomRepository {

    override suspend fun generateRoom(
        request: GenerateRoomRequest
    ): ResultState<GenerateRoomResult> {
        return try {
            val dto = roomService.generateRoom(
                initImage  = request.initImage,
                prompt = request.prompt,
                strength = request.strength
            )
            ResultState.Success(dto.toDomain())
        } catch (e: Exception) {
            println("DEBUG_REPO: ERROR -> ${e.message}")
            ResultState.Failure(e.message ?: "Unknown error")
        }
    }

    override suspend fun fetchResult(
        fetchUrl: String
    ): ResultState<GenerateRoomResult> {
        return try {
            val dto = roomService.fetchGeneratedRoom(fetchUrl)
            ResultState.Success(dto.toDomain())
        } catch (e: Exception) {
            println("DEBUG_REPO: FETCH ERROR -> ${e.message}")
            ResultState.Failure(e.message ?: "Unknown error")
        }
    }
}