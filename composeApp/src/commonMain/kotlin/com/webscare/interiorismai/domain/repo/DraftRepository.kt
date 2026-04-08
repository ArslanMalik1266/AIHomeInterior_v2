package com.webscare.interiorismai.domain.repo

import kotlinx.coroutines.flow.Flow
import com.webscare.interiorismai.data.local.entities.DraftEntity

interface DraftsRepository {
    fun getAllDrafts(): Flow<List<DraftEntity>>
    suspend fun saveDraft(draft: DraftEntity)
    suspend fun deleteDraft(draft: DraftEntity)
    suspend fun deleteDraftById(id: Long)
}