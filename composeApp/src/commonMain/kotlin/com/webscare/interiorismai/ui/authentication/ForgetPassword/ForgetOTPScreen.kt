package com.webscare.interiorismai.ui.authentication.ForgetPassword


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
import com.webscare.interiorismai.ui.theme.app_color
import com.webscare.interiorismai.ui.theme.green_border
import com.webscare.interiorismai.ui.theme.green_btn
import com.webscare.interiorismai.ui.theme.grey_border
import com.webscare.interiorismai.ui.theme.smallText

@Composable
fun ForgetOTPRoot(onBackClick: () -> Unit, authViewModel: AuthViewModel, onSuccess: () -> Unit) {
    val state by authViewModel.state.collectAsState()
    ForgetOTPScreen(onBackClick,state, authViewModel.uiEvent, authViewModel::onRegisterFormEvent, onSuccess)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgetOTPScreen(
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        if (state.forgetPasswordVerifyResponse is ResultState.Loading) {
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
                text = "Forget Password",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = app_color
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Recover you password if you have forgot the password!",
                fontSize = 14.sp,
                color = smallText,
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Verification Code",
                fontSize = 14.sp,
                color = smallText,
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = state.otp,
                onValueChange = { event(RegisterEvent.OTPUpdate(it)) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        "EX. 123456",
                        color = grey_border
                    )
                },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = grey_border,
                    focusedBorderColor = green_border
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    event(RegisterEvent.ForgetPasswordVerify)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = green_btn
                )
            ) {
                Text(
                    text = "Verify OTP",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
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
                            event(RegisterEvent.ResendForget)
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