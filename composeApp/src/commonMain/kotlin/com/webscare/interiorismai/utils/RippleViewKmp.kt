package com.webscare.interiorismai.utils

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import kotlinx.datetime.Clock

@Composable
fun RippleViewKmp(
    modifier: Modifier = Modifier,
    rippleColor: Color = Color.White.copy(alpha = 0.1f), // 0x1A is ~10% alpha
    rippleCount: Int = 4,
    durationMs: Int = 3200
) {
    // Infinite transition animation progress (0.0 to 1.0) handle karega
    val infiniteTransition = rememberInfiniteTransition(label = "ripple")

    // Har ripple ke liye stagger delay ke sath animation start karein
    val ripples = (0 until rippleCount).map { index ->
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = durationMs,
                    delayMillis = (index * (durationMs / rippleCount)),
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Restart
            ),
            label = "ripple_$index"
        )
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val maxRadius = (size.minDimension / 2f) - 1f

        // Clip strictly to the canvas bounds
        clipRect {
            ripples.forEach { progressState ->
                val progress = progressState.value

                // 1. Easing Logic (InOutCubic)
                val eased = if (progress < 0.5f) {
                    4f * progress * progress * progress
                } else {
                    1f - ((-2f * progress + 2f).let { it * it * it }) / 2f
                }

                val radius = eased * maxRadius

                // 2. Fade Envelope (60% in, 40% out)
                val fade = when {
                    progress < 0.6f -> progress / 0.6f
                    else -> 1f - (progress - 0.6f) / 0.4f
                }

                // 3. Draw the ripple
                drawCircle(
                    color = rippleColor.copy(alpha = rippleColor.alpha * fade),
                    radius = radius,
                    center = androidx.compose.ui.geometry.Offset(cx, cy)
                )
            }
        }
    }
}