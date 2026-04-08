package com.webscare.interiorismai.ui.UiUtils

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CommonAppButton(title: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = {
            onClick()
        },
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF81C784)),
        shape = RoundedCornerShape(24.dp),
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 13.dp)
    ) {
        Text(text = title, fontSize = 15.sp, color = Color.White, fontWeight = FontWeight.Bold)
    }
}