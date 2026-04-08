package com.webscare.interiorismai.ui.UiUtils

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ProgressIndicator(
    currentStep: Int,
    totalSteps: Int,
    selectedColor: Color = Color(0xFF81C784),
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(totalSteps) { index ->
            val isSelected = currentStep == index

            // Animate width
            val width by animateDpAsState(
                targetValue = if (isSelected) 33.dp else 9.dp,
                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
            )

            // Animate height
            val height by animateDpAsState(
                targetValue = 9.dp,
                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
            )

            // Animate color
            val color by animateColorAsState(
                targetValue = if (isSelected)
                    selectedColor
                else
                    Color(0xFFEEEEEE),
                animationSpec = tween(durationMillis = 300)
            )

            Box(
                modifier = Modifier
                    .padding(horizontal = 3.dp)
                    .size(width = width, height = height)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}

