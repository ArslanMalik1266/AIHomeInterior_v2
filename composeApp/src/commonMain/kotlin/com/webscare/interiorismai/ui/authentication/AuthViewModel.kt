package com.webscare.interiorismai.ui.authentication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.russhwolf.settings.Settings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.webscare.interiorismai.data.remote.util.ResultState
import com.webscare.interiorismai.domain.model.GuestSession
import com.webscare.interiorismai.domain.model.UserDetail
import com.webscare.interiorismai.domain.repo.AuthRepository
import com.webscare.interiorismai.domain.usecase.LoginUseCase
import com.webscare.interiorismai.domain.usecase.LogoutUseCase
import com.webscare.interiorismai.domain.usecase.RegisterGuestUseCase
import com.webscare.interiorismai.domain.usecase.ResendOtpUseCase
import com.webscare.interiorismai.domain.usecase.VerifyOtpUseCase
import com.webscare.interiorismai.ui.authentication.register.RegisterEvent
import com.webscare.interiorismai.ui.authentication.register.RegisterState
import com.webscare.interiorismai.ui.common.base.CommonUiEvent
import com.webscare.interiorismai.ui.common.base.CommonUiEvent.*
import com.webscare.interiorismai.utils.Constants
import com.webscare.interiorismai.utils.getDeviceId

class AuthViewModel(private val verifyOtpUseCase: VerifyOtpUseCase,
                    private val loginUseCase: LoginUseCase,
                    private val logoutUseCase: LogoutUseCase,
                    private val resendOtpUseCase: ResendOtpUseCase,
                    private val registerGuestUseCase: RegisterGuestUseCase,
                    val repository: AuthRepository, val settings: Settings) : ViewModel() {
    private val _state = MutableStateFlow(RegisterState())
    val state: StateFlow<RegisterState> = _state.asStateFlow()

    private val _user = MutableStateFlow<UserDetail?>(null)
    val user = _user.asStateFlow()


    private val _guestSession = MutableStateFlow<GuestSession?>(null)
    val guestSession = _guestSession.asStateFlow()

    private val _uiEvent = MutableSharedFlow<CommonUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()
    private var timerJob: Job? = null

    init {
        val savedEmail = settings.getString("user_email", "")
        _state.value = _state.value.copy(email = savedEmail)

        if (savedEmail.isNotBlank()) {
            fetchUserDetails()
        } else {
            registerGuest()
        }
    }

    fun registerGuest() {
        viewModelScope.launch {
            val deviceId = getDeviceId()
            println("DEBUG_DEVICE_ID: = $deviceId")
            val result = registerGuestUseCase(
                packageName = "com.webscare.interiorismai",
                deviceId = getDeviceId()

            )

            when (result) {
                is ResultState.Success -> {

                    val session = result.data
                    if (!session.isNew && session.user.userEmail != null) {
                        _state.update {
                            it.copy(
                                email = session.user.userEmail!!,
                                freeCredits = session.freeCredits,
                                totalCredits = session.totalCredits
                            )
                        }
                        fetchUserDetails()
                    } else {
                        _guestSession.value = session
                        _state.update {
                            it.copy(
                                freeCredits = session.freeCredits,
                                totalCredits = session.totalCredits
                            )
                        }
                    }
                    println("DEBUG_GUEST: Registered! Credits: ${session.freeCredits}, isNew: ${session.isNew}")
                }
                is ResultState.Failure -> {
                    println("DEBUG_GUEST: Registration Failed: ${result.msg}")
                }
                else -> {
                    println("DEBUG_GUEST: Register check:  isNew:")


                }
            }
        }
    }
    fun onAuthEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.FetchUserDetails -> {
                fetchUserDetails()
            }
            else -> onRegisterFormEvent(event)
        }
    }

    fun fetchUserDetails() {
        val savedEmail = settings.getString("user_email", "")
        if (savedEmail.isBlank()) return

        viewModelScope.launch(Dispatchers.IO) {
            delay(800)

            _state.update { it.copy(isLoading = true) }

            val result = if (savedEmail.isNotBlank()) {
                // Logged-in user
                repository.getProfileAndCredits(
                    packageName = "com.webscare.interiorismai",
                    deviceId = getDeviceId(),
                    userEmail = savedEmail,
                    authProvider = "email"
                )
            } else {
                // Guest user - device-based fetch
                repository.getProfileAndCredits(
                    packageName = "com.webscare.interiorismai",
                    deviceId = getDeviceId(),
                    userEmail = "",
                    authProvider = "guest"
                )
            }
            withContext(Dispatchers.Main) {
                result.onSuccess { deviceLinkResult ->

                    _state.update { currentState ->
                        currentState.copy(
                            freeCredits = deviceLinkResult.freeCredits ?: 0,
                            purchaseCredits = deviceLinkResult.purchaseCredits ?: 0,
                            totalCredits = deviceLinkResult.totalCredits ?: 0,
                            isLoading = false
                        )
                    }

                    // 4. User details ko bhi update karein taake profile sync rahe
                    _user.value = deviceLinkResult.user


                    println("DEBUG: Credits Refreshed -> Total: ${deviceLinkResult.totalCredits}")

                }.onFailure { error ->
                    _state.update { it.copy(isLoading = false) }
                    _uiEvent.emit(CommonUiEvent.ShowError("Sync Failed: ${error.message}"))
                }
            }
        }
    }
    fun onRegisterFormEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.EmailUpdate -> _state.value = _state.value.copy(email = event.email)

            is RegisterEvent.OTPUpdate -> _state.value = _state.value.copy(otp = event.otp)

            RegisterEvent.Login -> {
                if (_state.value.email.isBlank()) {
                    viewModelScope.launch { _uiEvent.emit(ShowError("Email is required")) }
                } else {
                    performLogin()
                }
            }

            RegisterEvent.Verify -> {
                if (_state.value.otp.isBlank()) {
                    viewModelScope.launch { _uiEvent.emit(ShowError("OTP is required")) }
                } else {
                 verifyOtp()
                }
            }

            RegisterEvent.Resend -> {
                if (state.value.canResend) {
                    performResendOtp()
                }
            }
            RegisterEvent.StartTimer -> {
                startResendTimer()
            }
            // Baaki events jo aapne maange nahi wo ignore kar diye
            else -> {}
        }
    }

    private var isResendLoading = false

    private fun performResendOtp() {
        // ✅ HARD GUARD (prevents spam + race conditions)
        if (isResendLoading || !_state.value.canResend) return

        viewModelScope.launch {
            isResendLoading = true

            try {
                // ✅ Start timer immediately (better UX + blocks further clicks)
                startResendTimer()

                val result = resendOtpUseCase(
                    packageName = "com.webscare.interiorismai",
                    deviceId = getDeviceId(),
                    userEmail = _state.value.email,
                    authProvider = "email"
                )

                result.onSuccess {
                    _uiEvent.emit(CommonUiEvent.ShowSuccess("OTP Resent Successfully!"))
                }.onFailure { e ->
                    _uiEvent.emit(CommonUiEvent.ShowError(e.message ?: "Failed to resend OTP"))
                }

            } catch (e: Exception) {
                _uiEvent.emit(CommonUiEvent.ShowError(e.message ?: "Unexpected error"))
            } finally {
                // ✅ ALWAYS reset (even if crash happens)
                isResendLoading = false
            }
        }
    }

    private fun verifyOtp() {
        viewModelScope.launch {
            _state.value = _state.value.copy(deviceLinkResponse = ResultState.Loading)

            val result = verifyOtpUseCase(
                packageName = "com.webscare.interiorismai",
                deviceId = getDeviceId(),
                userEmail = _state.value.email,
                authProvider = "email",
                otp = _state.value.otp
            )

            result.onSuccess { deviceLinkResult ->
                println("DEBUG: freeCredits = ${deviceLinkResult.freeCredits}")
                println("DEBUG: purchaseCredits = ${deviceLinkResult.purchaseCredits}")
                println("DEBUG: totalCredits = ${deviceLinkResult.totalCredits}")

                println("DEBUG_VERIFY_OTP: DeviceLinkResult = $deviceLinkResult")
                _state.value = _state.value.copy(
                    deviceLinkResponse = ResultState.Success(deviceLinkResult),
                    freeCredits = deviceLinkResult.freeCredits ?: 0,
                    purchaseCredits = deviceLinkResult.purchaseCredits ?: 0,
                    totalCredits = deviceLinkResult.totalCredits ?: 0
                )
                _user.value = deviceLinkResult.user
                println("DEBUG_VERIFY_OTP: _user.value = ${_user.value}")
                if (deviceLinkResult.status == "linked") {
                    settings.putBoolean(Constants.LOGIN, true)
                    settings.putString("user_email", deviceLinkResult.userEmail ?: "")
                    _state.update { it.copy(email = deviceLinkResult.userEmail ?: "") }

                    _uiEvent.emit(ShowSuccess("Device Linked Successfully"))
                    _uiEvent.emit(NavigateToSuccess)
                }


            }.onFailure { exception ->
                val errorMsg = exception.message ?: "Verification Failed"
                println("DEBUG: ViewModel Verification Failed: $errorMsg")
                _state.value = _state.value.copy(deviceLinkResponse = ResultState.Failure(errorMsg))
                _uiEvent.emit(ShowError(errorMsg))
            }
        }
    }
    private fun performLogin() {
        println("DEBUG_LOGIN: 1. performLogin() started")
        viewModelScope.launch {
            println("DEBUG_LOGIN: 2. Current State before loading: ${_state.value.loginResponse}")
            _state.value = _state.value.copy(loginResponse = ResultState.Loading)

            val result = loginUseCase(
                packageName = "com.webscare.interiorismai",
                deviceId = getDeviceId(),
                userEmail = _state.value.email,
                authProvider = "email"
            )

            result.onSuccess { response ->
                println("DEBUG_LOGIN: 3. Success! Status: ${response.status}")
                _state.value = _state.value.copy(loginResponse = ResultState.Success(response))
                println("Login Response: $response")
                if (response.status == "otp_sent" ) {
                    println("DEBUG_LOGIN: 4. Emitting NavigateToSuccess")
                    _uiEvent.emit(CommonUiEvent.ShowSuccess(response.message ?: "OTP sent successfully"))
                    _uiEvent.emit(CommonUiEvent.NavigateToSuccess) // Ye Verification screen par le jayega
                } else {
                    _uiEvent.emit(CommonUiEvent.ShowError(response.message ?:"Login Failed"))
                }
            }.onFailure { exception ->
                println("DEBUG_LOGIN: 5. Failed: ${exception.message}")
                val errorMsg = exception.message ?: "Login Failed"
                println("DEBUG: ViewModel Login Failed: $errorMsg")
                _state.value = _state.value.copy(loginResponse = ResultState.Failure(errorMsg))
                _uiEvent.emit(CommonUiEvent.ShowError(errorMsg))
            }
        }
    }


    private fun startResendTimer() {
        timerJob?.cancel()
        _state.value = _state.value.copy(canResend = false, resendTimerSeconds = 60)
        timerJob = viewModelScope.launch {
            for (i in 60 downTo 0) {
                _state.update {
                    it.copy(resendTimerSeconds = i)
                }
                delay(1000)
            }
            _state.update {
                it.copy(
                    canResend = true,
                    resendTimerSeconds = 0
                )
            }
            }
        }
    fun onVerificationScreenOpened() {
        if (_state.value.resendTimerSeconds == 0 && !_state.value.canResend) {
            startResendTimer()
        }
    }


    fun logout() {
        println("DEBUG_LOGOUT: 1. Function Called")
        viewModelScope.launch {
            try {
                val savedEmail = settings.getString("user_email", "NOT_FOUND")
                println("DEBUG_LOGOUT: 2. Saved Email: $savedEmail")

                val devId = getDeviceId()
                println("DEBUG_LOGOUT: 3. Device ID: $devId")

                println("DEBUG_LOGOUT: 4. Calling logoutUseCase...")
                val result = logoutUseCase(
                    packageName = "com.webscare.interiorismai",
                    deviceId = devId,
                    userEmail = savedEmail,
                )

                result.onSuccess { domainModel ->
                    println("DEBUG_LOGOUT: 5. API Success! Status: ${domainModel.status}")
                    settings.putBoolean(Constants.LOGIN, false)
                    settings.remove("user_email")
                    println("DEBUG_LOGOUT: 6. Settings Cleared")

                    // ✅ Guest credits preserve karo
                    val guestFreeCredits = _guestSession.value?.freeCredits ?: 0
                    val guestTotalCredits = _guestSession.value?.totalCredits ?: 0

                    _state.value = RegisterState().copy(
                        freeCredits = guestFreeCredits,    // ✅ guest credits rakho
                        totalCredits = guestTotalCredits   // ✅ guest total rakho
                    )
                    _user.value = null
                    _uiEvent.emit(CommonUiEvent.NavigateAfterLogout)
                }                    .onFailure { error ->
                    println("DEBUG_LOGOUT: 5. API Failure: ${error.message}")
                    error.printStackTrace() // Isse poora error stack trace dikhega
                    _uiEvent.emit(CommonUiEvent.ShowError(error.message ?: "Logout Failed"))
                }
            } catch (e: Exception) {
                println("DEBUG_LOGOUT: CRASH in ViewModel: ${e.message}")
                _uiEvent.emit(CommonUiEvent.ShowError("Unexpected Error: ${e.message}"))
            }
        }
    }

}