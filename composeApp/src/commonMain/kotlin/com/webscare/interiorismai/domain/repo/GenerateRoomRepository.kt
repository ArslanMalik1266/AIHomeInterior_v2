package com.webscare.interiorismai.domain.repo

import com.webscare.interiorismai.data.remote.util.ResultState
import com.webscare.interiorismai.domain.model.GenerateRoomRequest
import com.webscare.interiorismai.domain.model.GenerateRoomResult

interface GenerateRoomRepository {
    suspend fun generateRoom(request: GenerateRoomRequest): ResultState<GenerateRoomResult>
    suspend fun fetchResult(fetchUrl: String): ResultState<GenerateRoomResult>
}