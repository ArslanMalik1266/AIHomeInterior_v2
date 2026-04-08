package com.webscare.interiorismai.utils

interface Logger {
    fun d(tag: String, message: String)
    fun e(tag: String, message: String)
}

// a singleton for easy use
//expect val LoggerImpl: Logger
