package com.webscare.interiorismai.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.webscare.interiorismai.data.local.entities.UserInfoEntity
import com.webscare.interiorismai.utils.Constants

@Dao
interface ProfileDao {
    @Query("SELECT * FROM  ${Constants.TABLE_USERS}")
    fun getUserInfo(): UserInfoEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUserInfo(userInfoEntity: UserInfoEntity)
}