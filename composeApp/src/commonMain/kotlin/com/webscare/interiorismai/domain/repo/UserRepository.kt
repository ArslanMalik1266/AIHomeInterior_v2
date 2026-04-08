package com.webscare.interiorismai.domain.repo

import com.webscare.interiorismai.data.local.entities.UserInfoEntity

interface UserRepository{
    suspend fun getUserProfileData(): UserInfoEntity
}