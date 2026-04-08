package com.webscare.interiorismai.ui.UiUtils


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.webscare.interiorismai.ui.theme.buttonBack

@Composable
fun ProgressLoading(progressBarColor: Color = buttonBack) {
    Dialog(onDismissRequest = {

    }) {
        Card(
            modifier = Modifier
                .size(120.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 10.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                androidx.compose.material3.CircularProgressIndicator(
                    color = progressBarColor,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    }
}