package com.webscare.interiorismai.ui.authentication.register

import com.webscare.interiorismai.data.remote.util.ResultState
import com.webscare.interiorismai.domain.model.DeviceLinkResult
import com.webscare.interiorismai.domain.model.RegisterResponse
import com.webscare.interiorismai.domain.model.VerifyResponse

data class RegisterState(
    val registerResponse: ResultState<RegisterResponse> = ResultState.Stable,
    val resendResponse: ResultState<VerifyResponse> = ResultState.Stable,
    val verifyResponse: ResultState<VerifyResponse> = ResultState.Stable,
    val loginResponse: ResultState<VerifyResponse> = ResultState.Stable,
    val email: String = "",
    val password: String = "",
    val otp: String = "",
    val username: String = "",
    val showPassword: Boolean = false,
    val fieldError: String? = null,
    val resendTimerSeconds: Int = 0,
    val canResend: Boolean = true,
    val rememberMe: Boolean = false,
    val forgetPasswordRequestResponse: ResultState<RegisterResponse> = ResultState.Stable,
    val forgetPasswordResendResponse: ResultState<RegisterResponse> = ResultState.Stable,
    val forgetPasswordVerifyResponse: ResultState<RegisterResponse> = ResultState.Stable,
    val forgetPasswordResetResponse: ResultState<RegisterResponse> = ResultState.Stable,
    val newPassword: String = "",
    val deviceLinkResponse: ResultState<DeviceLinkResult> = ResultState.Stable,
    val freeCredits: Int = 0,
    val purchaseCredits: Int = 0,
    val totalCredits: Int = 0,
    val isLoading: Boolean = false,
    val result: DeviceLinkResult? = null,
    val error: String? = null
)