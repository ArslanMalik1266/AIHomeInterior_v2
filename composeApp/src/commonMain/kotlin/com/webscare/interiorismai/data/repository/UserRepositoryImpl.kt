package com.webscare.interiorismai.data.repository

import com.webscare.interiorismai.data.local.dao.ProfileDao
import com.webscare.interiorismai.data.local.entities.UserInfoEntity
import com.webscare.interiorismai.domain.repo.UserRepository

class UserRepositoryImpl(val userProfileDao: ProfileDao) : UserRepository {
    override suspend fun getUserProfileData(): UserInfoEntity {
        return userProfileDao.getUserInfo()
    }

}