package com.webscare.interiorismai.ui.OnBoarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel
import com.webscare.interiorismai.navigation.NavigationViewModel
import com.webscare.interiorismai.ui.authentication.AuthViewModel

@Composable
fun SplashScreen(navigationViewModel: NavigationViewModel = koinViewModel(), navController: NavHostController,
                 authViewModel: AuthViewModel) {
    val state by navigationViewModel.state.collectAsState()
    LaunchedEffect(state.startDestination) {
        if (state.startDestination.isNotBlank()) {
            delay(1000)
            navController.navigate(state.startDestination){
                popUpTo(0) { inclusive = true }
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    0f to Color(0xFFC5EBB2),
                    0.38f to Color(0xFFDFF2C2),
                    0.73f to Color(0xFFC1DFB5),
                    1f to Color(0xFFD2F7BD)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Interiorism AI",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}