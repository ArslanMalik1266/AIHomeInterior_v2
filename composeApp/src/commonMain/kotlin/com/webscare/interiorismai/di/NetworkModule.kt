package com.webscare.interiorismai.di

import io.ktor.client.HttpClient
import org.koin.dsl.module
import com.webscare.interiorismai.data.remote.createHttpClientManual

val networkModule = module {

    single<HttpClient> { createHttpClientManual(get()) }
}