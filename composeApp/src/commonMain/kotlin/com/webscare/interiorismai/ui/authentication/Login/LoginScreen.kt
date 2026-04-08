package com.webscare.interiorismai.ui.authentication.Login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import homeinterior.composeapp.generated.resources.Res
import homeinterior.composeapp.generated.resources.emailicon
import kotlinx.coroutines.flow.SharedFlow
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import com.webscare.interiorismai.data.remote.util.ResultState
import com.webscare.interiorismai.navigation.Routes
import com.webscare.interiorismai.ui.authentication.AuthViewModel
import com.webscare.interiorismai.ui.authentication.register.RegisterEvent
import com.webscare.interiorismai.ui.authentication.register.RegisterState
import com.webscare.interiorismai.ui.UiUtils.BackIconButton
import com.webscare.interiorismai.ui.UiUtils.CustomSnackbar
import com.webscare.interiorismai.ui.UiUtils.ProgressLoading
import com.webscare.interiorismai.ui.UiUtils.rememberCustomSnackbarState
import com.webscare.interiorismai.ui.common.base.CommonUiEvent
import com.webscare.interiorismai.ui.theme.app_color
import com.webscare.interiorismai.ui.theme.green_border
import com.webscare.interiorismai.ui.theme.green_btn
import com.webscare.interiorismai.ui.theme.grey_border
import com.webscare.interiorismai.ui.theme.smallText
import com.webscare.interiorismai.ui.theme.white_color

@Composable
fun LoginRoot(authViewModel: AuthViewModel = koinViewModel(), navController: NavHostController, onBackClick: (() -> Unit)? = null) {
    val state by authViewModel.state.collectAsState()
    LoginScreen(navController, authViewModel.uiEvent, state, authViewModel::onRegisterFormEvent,onBackClick)
}

fun isValidEmail(email: String): Boolean {
    return email.endsWith("@gmail.com") && email.length > "@gmail.com".length
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavHostController,
    uiEvent: SharedFlow<CommonUiEvent>,
    state: RegisterState,
    onLoginEvent: (event: RegisterEvent) -> Unit,
    onBackClick: (() -> Unit)? = null
) {
    val isEmailValid = isValidEmail(state.email)

    val snackBarState = rememberCustomSnackbarState()
    LaunchedEffect(Unit) {
        uiEvent.collect { event ->
            println("DEBUG_UI_EVENT: Received Event -> $event")
            when (event) {
                is CommonUiEvent.ShowError -> {
                    snackBarState.showError(event.message)
                }

                CommonUiEvent.NavigateToSuccess -> {
                    navController.navigate(Routes.Verification) {
                    }
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(28.dp)
        ) {
            BackIconButton {
                if (onBackClick != null) {
                    onBackClick()
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Login",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = app_color
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Sign in to explore AI room makeovers, save your favorite styles, and transform your living space.",
                fontSize = 14.sp,
                color = smallText,
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Email field
            Text(
                text = "Email",
                fontSize = 14.sp,
                color = smallText,
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = state.email,
                onValueChange = { onLoginEvent(RegisterEvent.EmailUpdate(it)) },
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
            Spacer(modifier = Modifier.height(24.dp))

            // Login button
            Button(
                onClick = {
                    println("DEBUG_LOGIN_BTN: Button Clicked! Email = ${state.email}")
                    onLoginEvent(RegisterEvent.Login)
                },
                enabled = isEmailValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isEmailValid) green_btn else green_btn.copy(alpha = 0.4f),
                    disabledContainerColor = green_btn.copy(alpha = 0.4f)
                )
            ) {
                Text(
                    text = "Login",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isEmailValid) Color.White else Color.White.copy(alpha = 0.6f)
                )
            }

            HorizontalDivider(
                thickness = 1.dp,
                color = Color(0xffD2CECE),
                modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp, horizontal = 10.dp)
            )

        }
        if (state.loginResponse is ResultState.Loading) {
            ProgressLoading()
        }
        CustomSnackbar(
            state = snackBarState,
            duration = 3000L
        )
    }
}
