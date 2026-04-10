package com.webscare.interiorismai.ui.authentication.Verification

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.SharedFlow
import com.webscare.interiorismai.data.remote.util.ResultState
import com.webscare.interiorismai.ui.authentication.register.RegisterState
import com.webscare.interiorismai.ui.authentication.AuthViewModel
import com.webscare.interiorismai.ui.authentication.register.RegisterEvent
import com.webscare.interiorismai.ui.UiUtils.BackIconButton
import com.webscare.interiorismai.ui.UiUtils.ClickableText
import com.webscare.interiorismai.ui.UiUtils.CustomSnackbar
import com.webscare.interiorismai.ui.UiUtils.ProgressLoading
import com.webscare.interiorismai.ui.UiUtils.rememberCustomSnackbarState
import com.webscare.interiorismai.ui.common.base.CommonUiEvent
import com.webscare.interiorismai.ui.theme.buttonBack
import com.webscare.interiorismai.ui.theme.green_btn
import com.webscare.interiorismai.ui.theme.smallText


@Composable
fun VerificationRoot(
    onBackClick: () -> Unit,
    authViewModel: AuthViewModel,
    onSuccess: () -> Unit
) {
    val state by authViewModel.state.collectAsState()

    VerificationScreen(
        onBackClick = onBackClick,
        state = state,
        uiEvent = authViewModel.uiEvent,
        event = authViewModel::onRegisterFormEvent,
        onSuccess = onSuccess
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerificationScreen(
    onBackClick: () -> Unit,
    state: RegisterState,
    uiEvent: SharedFlow<CommonUiEvent>,
    event: (event: RegisterEvent) -> Unit,
    onSuccess: () -> Unit,
) {
    val snackBarState = rememberCustomSnackbarState()

    LaunchedEffect(Unit) {
        uiEvent.collect { event ->
            when (event) {
                is CommonUiEvent.ShowError -> {
                    snackBarState.showError(event.message)
                }

                CommonUiEvent.NavigateToSuccess -> {
                    onSuccess()
                }

                is CommonUiEvent.ShowSuccess -> {
                    snackBarState.showSuccess(event.message)
                }
                else -> {}
            }
        }
    }
    LaunchedEffect(Unit) {
        event(RegisterEvent.StartTimer)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        if (state.verifyResponse is ResultState.Loading) {
            ProgressLoading()
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .statusBarsPadding()
        ) {
            BackIconButton {
                onBackClick()
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Otp Verification",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = buttonBack
            )

            Text(
                text = "We sent a 6-digit verification code to ${maskEmail(state.email)}",
                fontSize = 14.sp,
                color = smallText,
                modifier = Modifier.padding(top = 8.dp),
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Verification Code",
                fontSize = 14.sp,
                color = smallText,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = state.otp,
                onValueChange = {
                    if (it.length <= 6) event(RegisterEvent.OTPUpdate(it)) // ✅ 6 se zyada nahi
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        "EX. 123456",
                        color = Color(0xFFCCCCCC)
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    focusedBorderColor = buttonBack
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- YAHAN CHANGE KIYA HAI ---
            Button(
                onClick = { event(RegisterEvent.Verify) },
                enabled = state.otp.length == 6,  // ← Boolean
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonBack,
                    disabledContainerColor = buttonBack.copy(alpha = 0.4f),
                    contentColor = Color.White,
                    disabledContentColor = Color.White.copy(alpha = 0.4f) // ← Color
                )
            ) {
                Text(
                    text = "Verify OTP",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Didn't get the code? ",
                    fontSize = 14.sp,
                    color = Color(0xFF666666),
                    lineHeight = 1.sp
                )
                AnimatedContent(
                    targetState = state.canResend,
                    transitionSpec = {
                        fadeIn() togetherWith fadeOut()
                    }
                ) { canResend ->
                    if (canResend) {
                        ClickableText(
                            title = "Click to resend",
                            textSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        ) {
                            event(RegisterEvent.Resend)
                        }
                    } else {
                        Text(
                            text = "Resend in ${state.resendTimerSeconds}s",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF999999)
                        )
                    }
                }
            }
        }
        CustomSnackbar(
            state = snackBarState,
            duration = 3000L
        )
    }
}
fun maskEmail(email: String): String {
    if (email.isBlank() || !email.contains('@')) return email
    val atIndex = email.indexOf('@')
    if (atIndex <= 3) return email

    val visiblePart = email.substring(0, 3)
    val maskedPart = "*".repeat(atIndex - 3)
    val domain = email.substring(atIndex)

    return visiblePart + maskedPart + domain
}