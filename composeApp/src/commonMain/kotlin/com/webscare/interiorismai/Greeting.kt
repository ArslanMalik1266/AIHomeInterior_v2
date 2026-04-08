package com.webscare.interiorismai

class Greeting {
    private val platform = getPlatform()

    fun greet(): String {
        return "Whats your version or platform, ${platform.name}!"
    }
}