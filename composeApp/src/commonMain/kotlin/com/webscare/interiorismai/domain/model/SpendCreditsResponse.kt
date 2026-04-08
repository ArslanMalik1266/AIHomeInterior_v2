package com.webscare.interiorismai.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class SpendCreditsResponse(
    val status: String? = null,
    val total_credits: Int? = null
)