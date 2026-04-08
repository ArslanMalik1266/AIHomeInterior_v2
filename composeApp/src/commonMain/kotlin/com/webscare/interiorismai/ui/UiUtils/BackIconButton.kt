package com.webscare.interiorismai.ui.UiUtils

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import homeinterior.composeapp.generated.resources.Res
import homeinterior.composeapp.generated.resources.arrow_back_
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import com.webscare.interiorismai.ui.theme.grey_color

@Composable
fun BackIconButton(image: DrawableResource = Res.drawable.arrow_back_, iconSize: Dp = 24.dp, tint: Color = grey_color, onClick: () -> Unit) {
    Box(modifier = Modifier.size(30.dp).clip(CircleShape).clickable {
        onClick()
    }, contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource(image),
            contentDescription = "Close",
            colorFilter = ColorFilter.tint(color = tint),
            modifier = Modifier.size(iconSize)
        )
    }
}