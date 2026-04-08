package com.webscare.interiorismai.utils

enum class GenerationStatus {
    IDLE,
    RUNNING,
    SUCCESS,    // 100% ho gaya ("Tap to View" dikhao)
    COMPLETED   // 5 second guzar gaye ya user ne click kar diya (Hide kar do)
}