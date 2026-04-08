package com.webscare.interiorismai.ui.UiUtils

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

class TopUShape(private val curveHeight: Dp = 32.dp) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val curvePx = with(density) { curveHeight.toPx() }
        val path = Path().apply {
            moveTo(0f, curvePx)
            quadraticTo(
                size.width / 2f, curvePx * 3,
                size.width, curvePx
            )
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }
        return Outline.Generic(path)
    }
}