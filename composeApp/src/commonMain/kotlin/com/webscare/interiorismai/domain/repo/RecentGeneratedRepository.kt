package com.webscare.interiorismai.domain.repo

import kotlinx.coroutines.flow.Flow
import com.webscare.interiorismai.data.local.entities.RecentGeneratedEntity

interface RecentGeneratedRepository {
    fun getRecentGenerated(): Flow<List<RecentGeneratedEntity>>
    suspend fun saveGenerated(generated: RecentGeneratedEntity): Long
    suspend fun deleteGeneratedById(id: Long)

}