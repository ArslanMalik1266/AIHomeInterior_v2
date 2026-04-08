package com.webscare.interiorismai.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.webscare.interiorismai.utils.Constants.TABLE_USERS

@Entity(tableName = TABLE_USERS)
data class UserInfoEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val fullname: String,
    val email: String,
    val token: String? = null,
)