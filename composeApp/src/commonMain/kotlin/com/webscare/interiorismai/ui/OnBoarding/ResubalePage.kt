package com.webscare.interiorismai.ui.OnBoarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource


@Composable
fun BoardingPage(image: DrawableResource) {
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val (fillFraction, bottomPadding) = when {
            maxWidth >= 840.dp || maxWidth > maxHeight -> 0.3f to 230.dp

            maxWidth >= 600.dp -> 0.6f to 350.dp

            else -> 1f to 160.dp
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 40.dp, end = 40.dp, bottom = bottomPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(image),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth(fillFraction),
                contentScale = ContentScale.Crop
            )
        }
    }
}



