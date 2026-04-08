package com.webscare.interiorismai.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeviceLinkResponseDto(
    val status: String? = null,
    @SerialName("user_email")
    val userEmail: String? = null,
    @SerialName("free_credits")
    val freeCredits: Int? = 0,
    @SerialName("purchased_credits")
    val purchasedCredits: Int? = 0,
    @SerialName("total_credits")
    val totalCredits: Int? = 0,
    val user: UserDto? = null, // Agar UserDto hai to usay bhi optional karein
    @SerialName("auth_provider")
    val authProvider: String? = null,
    val message: String? = null
)

@Serializable
data class UserDto(
    val id: Int,
    @SerialName("app_id") val appId: String,
    @SerialName("device_id") val deviceId: String,
    @SerialName("free_credits") val freeCredits: String,
    @SerialName("total_credits") val totalCredits: Int,
    @SerialName("user_email") val userEmail: String,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String
)


@Serializable
data class LogoutResponseDto(
    val status: String? = null,
    @SerialName("free_credits") val freeCredits: Int? = null,
    @SerialName("total_credits") val totalCredits: Int? = null,
    val user: UserDetailDto? = null
)

@Serializable
data class UserDetailDto(
    val id: Int? = null,
    @SerialName("app_id") val appId: String? = null,
    @SerialName("device_id") val deviceId: String? = null,
    @SerialName("free_credits") val freeCredits: String? = null,
    @SerialName("total_credits") val totalCredits: Int? = null,
    @SerialName("user_email") val userEmail: String? = null, // Will handle "null"
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)
