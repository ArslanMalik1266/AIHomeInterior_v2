package com.webscare.interiorismai.domain.model

import kotlinx.serialization.Serializable


@Serializable
data class VerifyResponse(
    val status: String,
    val message: String? = null,
    val auth_provider: String? = null,

)

