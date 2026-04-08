package com.webscare.interiorismai.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import com.webscare.interiorismai.data.local.entities.DraftEntity

@Dao
interface DraftDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDraft(draft: DraftEntity)

    // Saare drafts ko latest time ke hisaab se dikhane ke liye
    @Query("SELECT * FROM drafts ORDER BY createdAt DESC")
    fun getAllDrafts(): Flow<List<DraftEntity>>

    // Kisi specific draft ko delete karne ke liye
    @Delete
    suspend fun deleteDraft(draft: DraftEntity)

    // Agar aapko sirf limit mein data chahiye (e.g. sirf top 5 recent files)
    @Query("SELECT * FROM drafts ORDER BY createdAt DESC LIMIT 10")
    fun getRecentDrafts(): Flow<List<DraftEntity>>
    @Query("DELETE FROM drafts WHERE id = :id")
    suspend fun deleteDraftById(id: Long)
}