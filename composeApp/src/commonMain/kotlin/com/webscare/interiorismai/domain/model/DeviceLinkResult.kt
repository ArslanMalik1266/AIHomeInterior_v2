package com.webscare.interiorismai.domain.model

    data class DeviceLinkResult(
        val status: String? = null,
        val userEmail: String? = null,
        val freeCredits: Int? = null,
        val purchaseCredits: Int? = null,
        val totalCredits: Int? = null,
        val authProvider: String? = null,
        val user: UserDetail? = null
    )

    data class UserDetail(
        val id: Int? = null,
        val appId: String? = null,
        val deviceId: String? = null,
        val freeCredits: String? = null,
        val totalCredits: Int? = null,
        val userEmail: String? = null,
        val createdAt: String? = null,
        val updatedAt: String? = null
    )

    data class LogoutDomainModel(
        val status: String?,
        val freeCredits: Int?,
        val totalCredits: Int?,
        val user: UserDetailDomainModel?
    )

    data class UserDetailDomainModel(
        val id: Int?,
        val appId: String?,
        val deviceId: String?,
        val freeCredits: String?,
        val totalCredits: Int?,
        val userEmail: String?,
        val createdAt: String?,
        val updatedAt: String?
    )



