package com.webscare.interiorismai.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class RegisterResponse(
    val success: Boolean,
    val message: String,
    val error: String? = ""
)