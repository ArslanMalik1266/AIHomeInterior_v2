package com.webscare.interiorismai.ui.authentication

import com.webscare.interiorismai.domain.model.DeviceLinkResult

data class VerifyOtpState(
    val isLoading: Boolean = false,
    val result: DeviceLinkResult? = null,
    val error: String? = null
)