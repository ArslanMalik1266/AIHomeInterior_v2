package com.webscare.interiorismai.di

import org.koin.dsl.module
import com.webscare.interiorismai.data.remote.service.AuthService
import com.webscare.interiorismai.data.remote.service.RoomService
import com.webscare.interiorismai.data.repository.AuthRepositoryImpl
import com.webscare.interiorismai.data.repository.CreditsRepositoryImpl
import com.webscare.interiorismai.data.repository.InteriorsRepositoryImpl
import com.webscare.interiorismai.data.repository.RoomRepositoryImpl
import com.webscare.interiorismai.domain.repo.AuthRepository
import com.webscare.interiorismai.domain.repo.CreditsRepository
import com.webscare.interiorismai.domain.repo.InteriorsRepository
import com.webscare.interiorismai.domain.repo.RoomsRepository
import com.webscare.interiorismai.utils.NetworkConfig

val dataModule = module {
    single { NetworkConfig.API_KEY }

    single {
        AuthService(
            client = get(),
            baseUrl ="https://dashboard.urdufonts.com/api",
            apiKey = get()
        )
    }
    single {
        RoomService(
            client = get(),
            baseUrl = "https://interior.shabbirhussain.com/api"
        )
    }
    single<AuthRepository> { AuthRepositoryImpl(get())}
    single<RoomsRepository> { RoomRepositoryImpl(get()) }
    single<CreditsRepository> { CreditsRepositoryImpl(get()) }
    single<InteriorsRepository> { InteriorsRepositoryImpl(get()) }
}