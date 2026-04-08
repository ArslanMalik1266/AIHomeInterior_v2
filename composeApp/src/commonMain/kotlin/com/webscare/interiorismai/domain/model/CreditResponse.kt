package com.webscare.interiorismai.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreditResponse(
    val status: String? = null,
    @SerialName("purchased_credits")
    val purchasedCredits: Int? = 0,
    val message: String? = null
)

//@Serializable
//data class CreditResponseconsume(
//    val status: String,
//    val total_credits: Int,
//    val message: String? = null
//)