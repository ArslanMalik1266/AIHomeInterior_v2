package com.webscare.interiorismai.di

import org.koin.dsl.module
import com.webscare.interiorismai.domain.usecase.FetchGeneratedRoomUseCase
import com.webscare.interiorismai.domain.usecase.GenerateRoomUseCase
import com.webscare.interiorismai.domain.usecase.LoginUseCase
import com.webscare.interiorismai.domain.usecase.LogoutUseCase
import com.webscare.interiorismai.domain.usecase.RegisterGuestUseCase
import com.webscare.interiorismai.domain.usecase.ResendOtpUseCase
import com.webscare.interiorismai.domain.usecase.StartImageTrackingUseCase
import com.webscare.interiorismai.domain.usecase.VerifyOtpUseCase

val domainModule = module {
    factory { LoginUseCase(get()) }
    factory { VerifyOtpUseCase(get()) }
    factory { LogoutUseCase(get()) }
    factory { ResendOtpUseCase(get()) }
    factory { RegisterGuestUseCase(get()) }
    factory { GenerateRoomUseCase(get()) }
    factory { FetchGeneratedRoomUseCase(get()) }
    factory { StartImageTrackingUseCase(get()) }

}