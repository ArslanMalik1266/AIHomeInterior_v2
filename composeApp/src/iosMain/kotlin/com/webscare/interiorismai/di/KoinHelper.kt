package com.webscare.interiorismai.di

import org.koin.core.context.startKoin

class KoinHelper {
    fun doInitKoin() {
        startKoin {
            modules(appModule() + platformModule())
        }
    }
}
