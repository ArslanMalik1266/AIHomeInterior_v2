package com.webscare.interiorismai.data.repository

import kotlinx.coroutines.flow.Flow
import com.webscare.interiorismai.data.local.AppDatabase
import com.webscare.interiorismai.data.local.entities.DraftEntity
import com.webscare.interiorismai.domain.repo.DraftsRepository

class DraftsRepositoryImpl(private val db: AppDatabase) : DraftsRepository {
    override fun getAllDrafts(): Flow<List<DraftEntity>> = db.draftDao().getAllDrafts()

    override suspend fun saveDraft(draft: DraftEntity) = db.draftDao().insertDraft(draft)

    override suspend fun deleteDraft(draft: DraftEntity) = db.draftDao().deleteDraft(draft)
    override suspend fun deleteDraftById(id: Long) = db.draftDao().deleteDraftById(id)


}