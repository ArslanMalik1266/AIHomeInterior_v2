package com.webscare.interiorismai

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform