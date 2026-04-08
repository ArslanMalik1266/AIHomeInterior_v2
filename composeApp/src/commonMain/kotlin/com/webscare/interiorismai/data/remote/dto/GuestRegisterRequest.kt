package com.webscare.interiorismai.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GuestRegisterRequest(
    @SerialName("package_name") val packageName: String,
    @SerialName("device_id") val deviceId: String
)

@Serializable
data class GuestUserDto(
    @SerialName("id") val id: Int,
    @SerialName("app_id") val appId: String,
    @SerialName("device_id") val deviceId: String,
    @SerialName("free_credits") val freeCredits: String,
    @SerialName("total_credits") val totalCredits: Int,
    @SerialName("user_email") val userEmail: String?,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String
)

@Serializable
data class GuestSessionDto(
    @SerialName("status") val status: String,
    @SerialName("free_credits") val freeCredits: Int,
    @SerialName("total_credits") val totalCredits: Int,
    @SerialName("user") val user: GuestUserDto,
    @SerialName("is_new") val isNew: Boolean
)