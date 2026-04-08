package com.webscare.interiorismai.domain.usecase

import com.webscare.interiorismai.data.remote.util.ResultState
import com.webscare.interiorismai.domain.model.GenerateRoomResult
import com.webscare.interiorismai.domain.repo.GenerateRoomRepository

class FetchGeneratedRoomUseCase(
    private val repository: GenerateRoomRepository
) {
    suspend operator fun invoke(
        fetchUrl: String
    ): ResultState<GenerateRoomResult> {
        return repository.fetchResult(fetchUrl)
    }
}