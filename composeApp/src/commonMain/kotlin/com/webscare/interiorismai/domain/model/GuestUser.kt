package com.webscare.interiorismai.domain.model

data class GuestUser(
    val id: Int,
    val appId: String,
    val deviceId: String,
    val freeCredits: String,
    val totalCredits: Int,
    val userEmail: String?,
)

data class GuestSession(
    val status: String,
    val freeCredits: Int,
    val totalCredits: Int,
    val user: GuestUser,
    val isNew: Boolean
)