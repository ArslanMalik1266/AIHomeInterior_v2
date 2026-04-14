package com.webscare.interiorismai.ui.Generate.UiScreens


import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import homeinterior.composeapp.generated.resources.Res
import io.github.alexzhirkevich.compottie.Compottie
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter

@Composable
fun LoadingScreen() {
    val infiniteTransition = rememberInfiniteTransition(label = "loading")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White.copy(alpha = 0.8f))
            .clickable(
                indication = null, // Removes the ripple effect
                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
            ) {
                // Do nothing: this block prevents clicks from reaching the screen underneath
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier.size(226.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(
                    modifier = Modifier.size(174.dp)
                ) {
                    rotate(rotation) {
                        val gradientColors = listOf(
                            Color(0xFFF059EB),
                            Color(0xFFFFCF30),
                            Color(0xFFA9D35D)
                        )

                        drawArc(
                            brush = Brush.sweepGradient(
                                colors = gradientColors,
                                center = center
                            ),
                            startAngle = 0f,
                            sweepAngle = 360f,
                            useCenter = false,
                            style = Stroke(width = 7.5f, cap = StrokeCap.Round)
                        )
                    }
                }

                Canvas(
                    modifier = Modifier.size(120.dp)
                ) {
                    drawCircle(
                        color = Color.White,
                        radius = size.minDimension / 2
                    )
                }

                SparkleIcon()
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Processing your image...",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF575958)
            )
        }
    }
}


@Composable
fun SparkleIcon() {
    val composition by rememberLottieComposition {
        LottieCompositionSpec.JsonString(
            Res.readBytes("files/shinestar.json").decodeToString()
        )
    }
    Image(
        painter = rememberLottiePainter(
            composition = composition,
            iterations = Compottie.IterateForever
        ),
        contentDescription = "Lottie animation"
    )
}

