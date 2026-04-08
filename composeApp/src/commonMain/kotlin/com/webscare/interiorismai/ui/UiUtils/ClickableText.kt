package com.webscare.interiorismai.ui.UiUtils

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.webscare.interiorismai.ui.theme.green_border

@Composable
fun ClickableText(
    modifier: Modifier = Modifier,
    textSize: TextUnit = 14.sp,
    color: Color = green_border,
    title: String,
    fontWeight: FontWeight = FontWeight.Medium,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(5.dp))
            .clickable {
                onClick()
            }) {
        Text(
            text = title,
            color = color,
            fontSize = textSize,
            fontWeight = fontWeight,
            modifier = Modifier.padding(horizontal = 5.dp)
        )
    }
}