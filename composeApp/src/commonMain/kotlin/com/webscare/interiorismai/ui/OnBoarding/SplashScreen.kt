package com.webscare.interiorismai.ui.OnBoarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel
import com.webscare.interiorismai.navigation.NavigationViewModel
import com.webscare.interiorismai.ui.authentication.AuthViewModel
import homeinterior.composeapp.generated.resources.Res
import homeinterior.composeapp.generated.resources.blur_splash
import homeinterior.composeapp.generated.resources.interiorism_splash_icon
import homeinterior.composeapp.generated.resources.splash_design
import org.jetbrains.compose.resources.painterResource

@Composable
fun SplashScreen(
    navigationViewModel: NavigationViewModel = koinViewModel(), navController: NavHostController,
    authViewModel: AuthViewModel
) {
    val state by navigationViewModel.state.collectAsState()
    LaunchedEffect(state.startDestination) {
        if (state.startDestination.isNotBlank()) {
            delay(1000)
            navController.navigate(state.startDestination) {
                popUpTo(0) { inclusive = true }
            }
        }
    }
    val lightMinimalGreen = lerp(Color.White, Color(0xFFC8FFAC), 0.25f)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    0.0f to Color.White,
                    0.75f to Color.White,
                    1.0f to lightMinimalGreen
                )
            ),
    ) {

        Image(
            painter = painterResource(Res.drawable.splash_design),
            contentDescription = "Splash Design",
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.4f) // This forces 40% height
                .align(Alignment.TopCenter), // This pushes it to the top
            contentScale = ContentScale.Fit // Ensures image fills the area correctly
        )
        Image(
            painter = painterResource(Res.drawable.blur_splash),
            contentDescription = "Splash Design",
            modifier = Modifier
                .size(width = 500.dp, height = 350.dp)
                .align(Alignment.Center)
                .offset(y = (-100).dp),
            contentScale = ContentScale.Crop
        )

        Image(
            painter = painterResource(Res.drawable.interiorism_splash_icon),
            contentDescription = "Splash Image",
            modifier = Modifier
                .height(250.dp)
                .fillMaxWidth()
                .align(Alignment.Center),
            contentScale = ContentScale.Fit
        )
        Text(
            text = "Interiorism AI",
            fontSize = 26.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF3C3C3C),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp)
        )
    }
}