package com.webscare.interiorismai.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import com.webscare.interiorismai.domain.usecase.AddCreditsUseCase
import com.webscare.interiorismai.domain.usecase.FetchGeneratedRoomUseCase
import com.webscare.interiorismai.domain.usecase.GenerateRoomUseCase
import com.webscare.interiorismai.domain.usecase.RegisterGuestUseCase
import com.webscare.interiorismai.domain.usecase.SpendCreditsUseCase
import com.webscare.interiorismai.domain.usecase.SpendCreditsUseCaseGuest
import com.webscare.interiorismai.navigation.NavigationViewModel
import com.webscare.interiorismai.ui.authentication.AuthViewModel
import com.webscare.interiorismai.ui.CreateAndExplore.RoomsViewModel
import com.webscare.interiorismai.ui.OnBoarding.OnBoardingViewModel

val viewModelModule = module {
    factory { AddCreditsUseCase(get()) }
    factory { SpendCreditsUseCase(get()) }
    factory { RegisterGuestUseCase(get()) }
    factory { GenerateRoomUseCase(get()) }
    factory { FetchGeneratedRoomUseCase(get()) }
    factory { SpendCreditsUseCaseGuest(get()) }

    single {
        AuthViewModel(
            verifyOtpUseCase = get(),
            loginUseCase = get(),
            logoutUseCase = get(),
            resendOtpUseCase = get(),
            registerGuestUseCase = get(),
            repository = get(),
            settings = get()
        )
    }

    single {
        RoomsViewModel(
            roomsRepository = get(),
            addCreditsUseCase = get(),
            authViewModel = get(),
            draftsRepository = get(),
            recentGeneratedRepository = get(),
            spendCreditsUseCase = get(),
            generateRoomUseCase = get(),
            fetchGeneratedRoomUseCase = get(),
            httpClient = get(),
            spendCreditsUseCaseGuest = get(),
            startImageTrackingUseCase = get(),
            interiorsRepository = get()

        )
    }

    viewModelOf(::NavigationViewModel)
    viewModelOf(::OnBoardingViewModel)
}