package com.webscare.interiorismai.di

import coil3.ImageLoader
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import io.ktor.client.HttpClient
import org.koin.dsl.module
import com.webscare.interiorismai.data.remote.createHttpClientManual

val networkModule = module {

    single<HttpClient> { createHttpClientManual(get()) }

}