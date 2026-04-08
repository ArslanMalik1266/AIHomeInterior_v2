package com.webscare.interiorismai.data.mapper

import com.webscare.interiorismai.data.remote.dto.GuestSessionDto
import com.webscare.interiorismai.data.remote.dto.GuestUserDto
import com.webscare.interiorismai.domain.model.GuestSession
import com.webscare.interiorismai.domain.model.GuestUser

fun GuestSessionDto.toDomain(): GuestSession {
    return GuestSession(
        status = status,
        freeCredits = freeCredits,
        totalCredits = totalCredits,
        isNew = isNew,
        user = user.toDomain()
    )
}

fun GuestUserDto.toDomain(): GuestUser {
    return GuestUser(
        id = id,
        appId = appId,
        deviceId = deviceId,
        freeCredits = freeCredits,
        totalCredits = totalCredits,
        userEmail = userEmail
    )
}