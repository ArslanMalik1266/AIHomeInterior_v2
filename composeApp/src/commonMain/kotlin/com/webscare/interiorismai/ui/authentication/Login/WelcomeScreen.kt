package com.webscare.interiorismai.ui.authentication.Login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import homeinterior.composeapp.generated.resources.Res
import homeinterior.composeapp.generated.resources.emailicon
import homeinterior.composeapp.generated.resources.google
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.webscare.interiorismai.ui.UiUtils.ButtonWithIcon
import com.webscare.interiorismai.ui.theme.app_color
import com.webscare.interiorismai.ui.theme.green_border
import com.webscare.interiorismai.ui.theme.grey_border
import com.webscare.interiorismai.ui.theme.smallText

@Preview(showBackground = true)
@Composable
fun WelcomeScreen(
    onContinueWithGoogle: () -> Unit = {},
    onContinueWithEmail: () -> Unit = {},
    onLogin: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(28.dp),
        horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Welcome to",
            fontSize = 16.sp,
            color = smallText
        )

        Text(
            text = "Home Interior AI",
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 8.dp),
            color = app_color
        )

        Text(
            text = "Interiors shaped by your imagination, perfected by AI.",
            fontSize = 16.sp,
            color = smallText,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Let's Get Started...",
            fontSize = 16.sp,
            color = smallText
        )

        Spacer(modifier = Modifier.height(32.dp))

        ButtonWithIcon(image = Res.drawable.google, borderColor = green_border, title = "Continue with Google",
            onClick = fun() {

            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        ButtonWithIcon(image = Res.drawable.emailicon, borderColor = grey_border, title = "Continue with Email",     onClick = {
            onLogin()
        }
        )

        Spacer(modifier = Modifier.height(24.dp))

    }
}


