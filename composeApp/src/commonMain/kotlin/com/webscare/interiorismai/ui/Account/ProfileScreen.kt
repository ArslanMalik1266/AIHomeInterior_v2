package com.webscare.interiorismai.ui.Account

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import homeinterior.composeapp.generated.resources.Res
import homeinterior.composeapp.generated.resources.icon_profile
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import com.webscare.interiorismai.ui.UiUtils.BackIconButton
import com.webscare.interiorismai.ui.UiUtils.DeleteConfirmationDialog
import com.webscare.interiorismai.ui.authentication.AuthViewModel
import com.webscare.interiorismai.ui.common.base.CommonUiEvent
import com.webscare.interiorismai.ui.theme.green_btn
import com.webscare.interiorismai.ui.theme.grey_color
import com.webscare.interiorismai.ui.theme.white_color
import com.webscare.interiorismai.utils.addPressEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel = koinViewModel(),
    onBackClick: () -> Unit = {},
    onLogoutSuccess: () -> Unit = {},
    onLoginClick: () -> Unit = {}
) {


    var showDelete by remember {
        mutableStateOf(false)
    }
    var showLogoutDialog by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var notificationsEnabled by remember { mutableStateOf(true) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        authViewModel.uiEvent.collect { event ->
            when (event) {
                is CommonUiEvent.NavigateAfterLogout -> {
                    onLogoutSuccess()
                }
                is CommonUiEvent.NavigateToSuccess -> {
                    // Yahan kuch mat karo — ye Login/OTP flow ke liye hai
                }
                is CommonUiEvent.ShowError -> {
                    println("DEBUG_UI: Logout Error = ${event.message}")
                }
                is CommonUiEvent.ShowSuccess -> {
                    println("DEBUG_UI: Success = ${event.message}")
                }
            }
        }
    }
    val user by authViewModel.user.collectAsState()
    val isLoggedIn = user != null

    println("DEBUG_UI: ProfileScreen user data = $user")

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest =  { showLogoutDialog = false },
            confirmButton = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp) // Buttons ke darmiyan gap
                ) {
                    // Sign Out Button (Red Background)
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .addPressEffect {
                                // Logic moved inside the press effect
                                println("DEBUG_UI: Button clicked - Step 1")
                                showLogoutDialog = false
                                authViewModel.logout()
                                println("DEBUG_UI: Button clicked - Step 2")
                            },
                        color = Color(0xFFFB5C5C), // Red color
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                "Sign Out",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp
                            )
                        }
                    }

                    // Cancel Button (Light Grey Background)
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            // 👇 Replace the Button's internal onClick with your effect
                            .addPressEffect {
                                showLogoutDialog = false
                            },
                        color = Color(0xFFF5F5F5),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                "Cancel",
                                color = Color(0xFF808080),
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            },
            title = {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Leaving so soon?",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        color = Color(0xFF2C2C2C)
                    )
                }
            },
            text = {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "You'll be signed out of your account.",
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF615E5E)
                    )
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp)
        )
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp, top = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BackIconButton(iconSize = 18.dp, tint = Color(0xFF808080)) {
                    onBackClick()
                }
                Text(
                    text = "Profile",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2C2C2C),
                    lineHeight = 22.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            ProfileHeader(
                email = user?.userEmail ?: "Guest User"
            )

            Spacer(modifier = Modifier.height(60.dp))

            ProfileMenuItems(
                email = user?.userEmail ?: "Guest User",
                isLoggedIn = isLoggedIn,
                notificationsEnabled = notificationsEnabled,
                onNotificationToggle = { notificationsEnabled = it },
                onLogoutClick = { showLogoutDialog = true },
                onLoginClick = { onLoginClick() }
            )

        }
        if (showDelete) {
            ModalBottomSheet(
                onDismissRequest = { showDelete = false },
                sheetState = bottomSheetState,
                containerColor = Color.Transparent,
                dragHandle = null, modifier = Modifier.statusBarsPadding()
            ) {
                DeleteConfirmationDialog(   title = "Leaving so soon?",
                    subtitle = "You'll be signed out of your account.") {
                    scope.launch {
                        bottomSheetState.hide()
                        showDelete = false
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileHeader( email: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(84.dp)
                .clip(CircleShape)
                .background(Color.White)
                .border(1.dp, color = green_btn, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(Res.drawable.icon_profile),
                contentDescription = "Profile Picture",
                modifier = Modifier,
                contentScale = ContentScale.Crop,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))


        Text(
            text = email,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF686666),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ProfileMenuItems(
    email: String,
    isLoggedIn: Boolean,
    notificationsEnabled: Boolean,
    onNotificationToggle: (Boolean) -> Unit,
    onLogoutClick: () -> Unit,
    onLoginClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 35.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ProfileMenuItem(
            label = "Email",
            value = email
        )

//
//        ProfileMenuItem(
//            label = "Restore Purchases",
//            value = null
//        )

        if (isLoggedIn) {
            ProfileMenuItem(
                label = "Sign Out",
                value = null,
                isDestructive = true,
                onClick = onLogoutClick
            )
        } else {
            ProfileMenuItem(
                label = "Login",
                value = null,
                isDestructive = false,
                onClick = onLoginClick
            )
        }
    }
}

@Composable
fun ProfileMenuItem(
    label: String,
    value: String?,
    isDestructive: Boolean = false, // Color change for Sign Out
    onClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .addPressEffect {
                if (value == null) {
                    onClick()
                }
            }
            .padding(top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column {
            Text(
                text = label,
                fontSize = if (value != null) 16.sp else 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF4D4D4D)
            )

            if (value != null) {
                Text(
                    text = value,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF726F6F)
                )
            }
        }

        Divider(
            color = Color(0xFFE4E4E4),
            thickness = 0.5.dp
        )
    }
}