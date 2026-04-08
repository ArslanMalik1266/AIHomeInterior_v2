package com.webscare.interiorismai.ui.Generate.UiScreens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import homeinterior.composeapp.generated.resources.Res
import homeinterior.composeapp.generated.resources.roomplaceholder
import org.jetbrains.compose.resources.painterResource

@Composable
fun FirstPage(imageBytes: ByteArray?,
              imageUri: String? = null) {
    Box(
        modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.9f)
                .clip(RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.TopCenter
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalPlatformContext.current)
                    .data(imageBytes ?: imageUri)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(Res.drawable.roomplaceholder),
                error = painterResource(Res.drawable.roomplaceholder),
                contentDescription = "Selected Room Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }
    }
}