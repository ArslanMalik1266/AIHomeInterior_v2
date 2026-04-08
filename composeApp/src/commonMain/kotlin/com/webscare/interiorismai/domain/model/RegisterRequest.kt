package com.webscare.interiorismai.domain.model

data class RegisterRequest(
    val fullname: String,
    val email: String,
    val password: String
)
