package com.webscare.interiorismai.data.repository

import kotlinx.coroutines.flow.Flow
import com.webscare.interiorismai.data.local.dao.RecentGeneratedDao
import com.webscare.interiorismai.data.local.entities.RecentGeneratedEntity
import com.webscare.interiorismai.domain.repo.RecentGeneratedRepository

class RecentGeneratedRepositoryImpl(
    private val recentGeneratedDao: RecentGeneratedDao
) : RecentGeneratedRepository {

    override fun getRecentGenerated(): Flow<List<RecentGeneratedEntity>> {
        return recentGeneratedDao.getRecentGenerated()
    }

    override suspend fun saveGenerated(generated: RecentGeneratedEntity): Long {  // ← Long return
        println("🟡 REPO_SAVE: Saving entity...")
        println("🟡 REPO_SAVE:   - ID = ${generated.id}")

        val newId = recentGeneratedDao.insertGenerated(generated)

        println("🟡 REPO_SAVE: ✅ Inserted with ID = $newId")
        return newId
    }

    override suspend fun deleteGeneratedById(id: Long) {
        recentGeneratedDao.deleteGeneratedById(id)
    }

}