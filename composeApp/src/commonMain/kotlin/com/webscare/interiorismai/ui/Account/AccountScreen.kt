package com.webscare.interiorismai.ui.Account

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LifecycleResumeEffect
import homeinterior.composeapp.generated.resources.Res
import homeinterior.composeapp.generated.resources.coin
import homeinterior.composeapp.generated.resources.ic_restore
import homeinterior.composeapp.generated.resources.icon_profile
import homeinterior.composeapp.generated.resources.keyboard_arrow_down_24px
import homeinterior.composeapp.generated.resources.keyboard_arrow_up_24px
import homeinterior.composeapp.generated.resources.settingback
import org.jetbrains.compose.resources.painterResource
import com.webscare.interiorismai.ui.authentication.AuthViewModel
import com.webscare.interiorismai.ui.theme.grey_color
import com.webscare.interiorismai.utils.NotificationManager
import com.webscare.interiorismai.utils.SettingsManager
import com.webscare.interiorismai.utils.clickable
import com.webscare.interiorismai.utils.getAppVersion
import com.webscare.interiorismai.utils.getDeviceInfo
import com.webscare.interiorismai.utils.openEmail
import com.webscare.interiorismai.utils.openPlayStore
import com.webscare.interiorismai.utils.openUrl

@Composable
fun AccountScreen(
    viewModel: AuthViewModel,
    onSubscriptionClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
) {

    val state by viewModel.state.collectAsState()
    var isEnabled by remember { mutableStateOf(NotificationManager.isNotificationsEnabled()) }

    LifecycleResumeEffect(Unit) {
        viewModel.fetchUserDetails()
        onPauseOrDispose { }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, bottom = 20.dp, end = 20.dp, top = 14.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        )
        {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "Account",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2C2C2C),
                    lineHeight = 22.sp
                )

                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF4F4F4))
                        .border(1.dp, Color(0xFFF5F5F5), CircleShape)
                        .clickable { onProfileClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(Res.drawable.icon_profile),
                        contentDescription = "Profile Picture",
                        modifier = Modifier.fillMaxSize(0.5f),
                        contentScale = ContentScale.Fit,
                    )
                }
            }

            Text(
                text = "Manage your plan",
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Gray,
                lineHeight = 16.sp,
                modifier = Modifier.offset(y = (-8).dp)

            )
        }

        CreditCard(
            freeCredits = state.freeCredits,
            purchaseCredits = state.purchaseCredits,
            totalCredits = state.totalCredits
        ) {
            onSubscriptionClick()
        }

        Spacer(modifier = Modifier.height(10.dp))
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            NotificationsToggle()


//            ModelsSection()

            Spacer(modifier = Modifier.height(5.dp))

            AppInfoSection()

            Spacer(modifier = Modifier.height(40.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                Text(
                    text = getAppVersion(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = grey_color,
                    lineHeight = 16.sp
                )
                Text(
                    text = "© 2026 WebsCare Pvt. Ltd. All rights reserved.",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = grey_color,
                    lineHeight = 16.sp
                )
            }

        }

    }
}

@Composable
fun CreditCard(
    freeCredits: Int,
    purchaseCredits: Int,
    totalCredits: Int,
    onSubscriptionClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .height(100.dp)
    ) {

        Image(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(7.dp)),
            painter = painterResource(Res.drawable.settingback),
            contentDescription = "setback",
            contentScale = ContentScale.FillHeight
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .align(Alignment.BottomCenter)
                .padding(end = 16.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier.padding(start = 6.dp)
                    .width(80.dp)
                    .fillMaxHeight()
            ) {
                Image(
                    painter = painterResource(Res.drawable.coin),
                    contentDescription = "Coins",
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .offset(y = (-12).dp)
                )
            }

            // Main content
            Row(
                modifier = Modifier
                    .weight(1f).padding(start = 6.dp, bottom = 15.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(3.dp))
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFFFFF080),
                                        Color(0xFFEBD744)
                                    )
                                )
                            )
                            .padding(horizontal = 1.dp)
                    ) {
                        Text(
                            text = "Free",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF737373),
                            lineHeight = 10.sp
                        )
                    }

                    Text(
                        text = "$totalCredits Credits Left",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF355300),
                        letterSpacing = (-0.5).sp
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF71BA47))
                            .clickable(enabled = true, onClick = {
                                onSubscriptionClick()
                            })
                    ) {
                        Text(
                            text = "Buy more Credits",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }

            }
//            Box(modifier = Modifier.fillMaxHeight(0.9f), contentAlignment = Alignment.TopCenter) {
//                Row(
//                    verticalAlignment = Alignment.CenterVertically,
//                    horizontalArrangement = Arrangement.spacedBy(4.dp)
//                ) {
//                    Icon(
//                        modifier = Modifier.size(8.dp),
//                        painter = painterResource(Res.drawable.ic_restore),
//                        contentDescription = null,
//                        tint = Color.Unspecified
//                    )
//                    Icon(
//                        modifier = Modifier.size(8.dp),
//                        painter = painterResource(Res.drawable.ic_restore),
//                        contentDescription = null,
//                        tint = Color.Unspecified
//                    )
//                    Icon(
//                        modifier = Modifier.size(8.dp),
//                        painter = painterResource(Res.drawable.ic_restore),
//                        contentDescription = null,
//                        tint = Color.Unspecified
//                    )
//                    Text(
//                        text = "Restore purchases",
//                        fontSize = 9.sp,
//                        fontWeight = FontWeight.Medium,
//                        color = Color(0xFF466D00)
//                    )
//                }
//            }
        }
    }
}

@Composable
fun NotificationsToggle() {
    var isEnabled by remember { mutableStateOf<Boolean>(SettingsManager.isNotificationsEnabled()) }

    // Colors derived from your XML/Image
    val orange = Color(0xFFA3B18A) // The green from your pic (or use 0xFFFF9800 for orange)
    val lightGrey = Color(0xFFE0E0E0)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Notifications",
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF4D4D4D)
        )

        // --- CUSTOM SWITCH START ---
        val trackColor by animateColorAsState(if (isEnabled) orange else lightGrey)
        val thumbOffset by animateDpAsState(if (isEnabled) 23.dp else 0.dp)

        Box(
            modifier = Modifier
                .size(width = 45.dp, height = 23.dp)
                .clip(RoundedCornerShape(22.5.dp))
                .background(trackColor)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    isEnabled = !isEnabled
                    SettingsManager.setNotificationsEnabled(isEnabled)
                },
            contentAlignment = Alignment.CenterStart
        ) {
            Box(
                modifier = Modifier
                    .offset(x = thumbOffset)
                    .size(23.dp)
                    .background(Color.White, CircleShape)
                    .border(
                        4.dp,
                        trackColor,
                        CircleShape
                    ) // This creates the "inset" look from your XML
            )
        }
        // --- CUSTOM SWITCH END ---
    }
}

@Composable
fun ModelsSection() {
    var selectedModel by remember { mutableStateOf("DesignNet" to "Basic") }
    var isExpanded by remember { mutableStateOf(false) }
    val models = listOf(
        "DesignNet" to "Basic",
        "RoomGen" to "Advance",
        "InteriorMind" to "Advance",
        "DecoraAI" to "Advance",
        "SpaceSense" to "Advance"
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Text(
            text = "Models",
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF4D4D4D), lineHeight = 18.sp

        )

        Text(
            text = "Choose how much detail you want in your design suggestion.",
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFFB1B0B0),
            letterSpacing = 0.sp, lineHeight = 16.sp

        )

        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .border(1.dp, Color(0xFFEAEAEA), RoundedCornerShape(10.dp))
                .background(Color.White)
        ) {
            // Selected Model Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = selectedModel.first,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF7A7A7A)
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                Color(0xFFE3FFD4)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = selectedModel.second,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFF3C5809),
                            lineHeight = 16.sp
                        )
                    }
                }

                Image(
                    painter = painterResource(
                        if (isExpanded)
                            Res.drawable.keyboard_arrow_up_24px
                        else
                            Res.drawable.keyboard_arrow_down_24px
                    ),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    colorFilter = ColorFilter.tint(Color(0xFF7A7A7A))
                )
            }

            // Expanded Options
            androidx.compose.animation.AnimatedVisibility(
                visible = isExpanded,
                enter = androidx.compose.animation.expandVertically() + androidx.compose.animation.fadeIn(),
                exit = androidx.compose.animation.shrinkVertically() + androidx.compose.animation.fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = Color(0xFFEAEAEA)
                    )

                    models.forEach { model ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedModel = model
                                    isExpanded = false
                                }
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = model.first,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF7A7A7A)
                            )

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        Color(0xFFE3FFD4)
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = model.second,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = Color(0xFF3C5809),
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppInfoSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Text(
            text = "App info",
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF4D4D4D),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .border(1.dp, Color(0xFFEAEAEA), RoundedCornerShape(10.dp))
                .background(Color.White)
                .padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
            Column(

            ) {
                AppInfoItem(
                    text = "Contact Support",
                    showArrow = true,
                    onClick = {
                        openEmail(
                            to = "hello@webscare.com",
                            subject = "Support Request",
                            body = ""
                        )
                    }
                )
                AppInfoItem("Help us Improve", showArrow = true,
                    onClick = {
                        openEmail(
                            to = "hello@webscare.com",
                            subject = "Feedback – Help Us Improve",
                            body = ""
                        )
                    })

                AppInfoItem("Request a feature", showArrow = true,
                    onClick = {
                        openEmail(
                            to = "hello@webscare.com",
                            subject = "Feature Request",
                            body = "Hi, I'd like to request the following feature:\n\n"
                        )
                    }
                )
                AppInfoItem(
                    "Privacy Policy", showArrow = true,
                    onClick = {
                        openUrl("http://webscare.com/privacy-policy-interiorism-ai/")
                    })
                AppInfoItem("Rate the App", showArrow = true,
                    onClick = {
                        openPlayStore("com.webscare.interiorismai")
                    }
                    )
                AppInfoItem("Report a bug", showDivider = false, showArrow = true,
                    onClick = {
                        openEmail(
                            to = "support@urducanvas.com",
                            subject = "Bug Report",
                            body = "Hi, I'd like to report the following bug:\n\n${getDeviceInfo()}\n\nDescription:\n"
                        )
                    }
                    )


            }
        }
    }
}

@Composable
fun AppInfoItem(
    text: String,
    showDivider: Boolean = true,
    onClick: () -> Unit = {},
    showArrow: Boolean = true,
) {

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()

                .clickable() { onClick() }
                .clip(RoundedCornerShape(8.dp))
                .padding(vertical = 15.dp, horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF4D4D4D)
            )

            if (showArrow) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = Color(0xFFB1B0B0),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        if (showDivider) {
            Divider(
                color = Color(0xFFE4E4E4),
                thickness = 0.5.dp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}