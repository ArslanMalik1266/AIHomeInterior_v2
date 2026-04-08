package com.webscare.interiorismai.ui.authentication.register

sealed interface RegisterEvent {
    data class NameUpdate(val name: String) : RegisterEvent
    data class EmailUpdate(val email: String) : RegisterEvent
    data class PasswordUpdate(val password: String) : RegisterEvent
    data class OTPUpdate(val otp: String) : RegisterEvent
    data object Register : RegisterEvent
    data object Login : RegisterEvent

    data object Verify : RegisterEvent
    data object Resend : RegisterEvent
    data object ResendForget : RegisterEvent
    data class TogglePassword(val newState: Boolean) : RegisterEvent
    object ForgetPasswordRequest : RegisterEvent
    object ForgetPasswordVerify : RegisterEvent
    object ForgetPasswordReset : RegisterEvent
    data class NewPasswordUpdate(val password: String) : RegisterEvent

    data object FetchUserDetails : RegisterEvent
}