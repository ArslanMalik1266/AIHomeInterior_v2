package com.webscare.interiorismai.domain.model

data class GenerateRoomRequest(
    val initImage: String,      // base64 ya URL
    val prompt: String,
    val strength: Float = 0.7f,
    val negativePrompt: String = "Blurry, low resolution, distorted proportions, perspective error, unrealistic lighting, messy, cluttered, poorly drawn furniture, floating objects, non-functional layout, extra legs on furniture, missing furniture pieces, low detail, pixelated, grainy, compression artifacts, oversaturated, chromatic aberration, asymmetry, cartoon, illustration, anime, CGI-look, out of frame, duplicate objects, watermark, text, signature, bad anatomy, warped textures, noisy, low-quality render."
)