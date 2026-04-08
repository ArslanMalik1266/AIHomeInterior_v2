package com.webscare.interiorismai.domain.usecase

import com.webscare.interiorismai.data.remote.util.ResultState
import com.webscare.interiorismai.domain.model.GenerateRoomRequest
import com.webscare.interiorismai.domain.model.GenerateRoomResult
import com.webscare.interiorismai.domain.repo.GenerateRoomRepository

class GenerateRoomUseCase(
    private val repository: GenerateRoomRepository
) {
    suspend operator fun invoke(
        request: GenerateRoomRequest
    ): ResultState<GenerateRoomResult> {
        return repository.generateRoom(request)
    }
}