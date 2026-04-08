package com.webscare.interiorismai.ui.UiUtils

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import com.webscare.interiorismai.ui.theme.black_color
import com.webscare.interiorismai.ui.theme.green_border
import com.webscare.interiorismai.ui.theme.white_color

@Composable
fun ButtonWithIcon(image: DrawableResource, title: String, borderColor: Color = green_border, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = white_color, RoundedCornerShape(16.dp))
            .border(BorderStroke(1.dp, borderColor), RoundedCornerShape(12.dp))
            .height(56.dp).clickable {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(image),
                contentDescription = "${image}",
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = black_color
            )
        }
    }
}