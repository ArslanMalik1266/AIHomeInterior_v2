package com.webscare.interiorismai.ui.authentication.ForgetPassword

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import homeinterior.composeapp.generated.resources.Res
import homeinterior.composeapp.generated.resources.emailicon
import kotlinx.coroutines.flow.SharedFlow
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import com.webscare.interiorismai.data.remote.util.ResultState
import com.webscare.interiorismai.ui.authentication.AuthViewModel
import com.webscare.interiorismai.ui.authentication.register.RegisterEvent
import com.webscare.interiorismai.ui.authentication.register.RegisterState
import com.webscare.interiorismai.ui.UiUtils.BackIconButton
import com.webscare.interiorismai.ui.UiUtils.ProgressLoading
import com.webscare.interiorismai.ui.UiUtils.rememberCustomSnackbarState
import com.webscare.interiorismai.ui.common.base.CommonUiEvent
import com.webscare.interiorismai.ui.theme.app_color
import com.webscare.interiorismai.ui.theme.black_color
import com.webscare.interiorismai.ui.theme.green_border
import com.webscare.interiorismai.ui.theme.green_btn
import com.webscare.interiorismai.ui.theme.grey_border
import com.webscare.interiorismai.ui.theme.smallText
import com.webscare.interiorismai.ui.theme.white_color

@Composable
fun ForgetEmailRoot(
    authViewModel: AuthViewModel = koinViewModel(),
    onBack: () -> Unit = {},
    onSuccess: () -> Unit = {}
) {
    val state by authViewModel.state.collectAsState()
    ForgotPasswordScreen(state, authViewModel.uiEvent, onBack, onSuccess, authViewModel::onRegisterFormEvent)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    state: RegisterState,
    uiEvent: SharedFlow<CommonUiEvent>,
    onBack: () -> Unit = {},
    onSuccess: () -> Unit,
    onAuthEvent: (event: RegisterEvent) -> Unit,
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
            .background(white_color)
            .statusBarsPadding()
    ) {
        if (state.forgetPasswordRequestResponse is ResultState.Loading) {
            ProgressLoading()
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            BackIconButton {
                onBack()
            }
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Forgot Password?",
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

            // Email field
            Text(
                text = "Email",
                fontSize = 14.sp,
                color = black_color,
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = state.email,
                onValueChange = { onAuthEvent(RegisterEvent.EmailUpdate(it)) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        "Ex. abc@example.com",
                        color = grey_border

                    )
                },
                leadingIcon = {
                    Image(
                        painter = painterResource(Res.drawable.emailicon),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(color = LocalContentColor.current)
                    )
                },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = grey_border,
                    focusedBorderColor = green_border,
                    focusedLeadingIconColor = green_border,
                    unfocusedLeadingIconColor = grey_border
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Submit button
            Button(
                onClick = {
                    onAuthEvent(RegisterEvent.ForgetPasswordRequest)
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
                    text = "Submit",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
