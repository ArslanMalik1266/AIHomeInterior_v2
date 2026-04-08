package com.webscare.interiorismai.utils

import coil3.ImageLoader
import coil3.PlatformContext
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.request.crossfade

fun createImageLoader(context: PlatformContext): ImageLoader {
    return ImageLoader.Builder(context)
        .components {
            add(KtorNetworkFetcherFactory())
        }
        .crossfade(true)
        .build()
}