package com.webscare.interiorismai.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import com.webscare.interiorismai.domain.usecase.AddCreditsUseCase
import com.webscare.interiorismai.domain.usecase.FetchGeneratedRoomUseCase
import com.webscare.interiorismai.domain.usecase.GenerateRoomUseCase
import com.webscare.interiorismai.domain.usecase.LoginUseCase
import com.webscare.interiorismai.domain.usecase.LoginWithGoogleUseCase
import com.webscare.interiorismai.domain.usecase.LogoutUseCase
import com.webscare.interiorismai.domain.usecase.RegisterGuestUseCase
import com.webscare.interiorismai.domain.usecase.ResendOtpUseCase
import com.webscare.interiorismai.domain.usecase.SpendCreditsUseCase
import com.webscare.interiorismai.domain.usecase.SpendCreditsUseCaseGuest
import com.webscare.interiorismai.domain.usecase.VerifyOtpUseCase
import com.webscare.interiorismai.navigation.NavigationViewModel
import com.webscare.interiorismai.ui.authentication.AuthViewModel
import com.webscare.interiorismai.ui.CreateAndExplore.RoomsViewModel
import com.webscare.interiorismai.ui.OnBoarding.OnBoardingViewModel
import org.koin.core.module.dsl.viewModel

val viewModelModule = module {
    factory { VerifyOtpUseCase(get()) }
    factory { LoginUseCase(get()) }
    factory { LogoutUseCase(get()) }
    factory { ResendOtpUseCase(get()) }
    factory { RegisterGuestUseCase(get()) }
    factory { LoginWithGoogleUseCase(get()) }
    factory { AddCreditsUseCase(get()) }
    factory { SpendCreditsUseCase(get()) }
    factory { SpendCreditsUseCaseGuest(get()) }
    factory { GenerateRoomUseCase(get()) }
    factory { FetchGeneratedRoomUseCase(get()) }

    viewModel {
        AuthViewModel(
            verifyOtpUseCase = get(),
            loginUseCase = get(),
            logoutUseCase = get(),
            resendOtpUseCase = get(),
            registerGuestUseCase = get(),
            repository = get(),
            settings = get(),
            googleSignInHelper = get(),
          loginWithGoogleUseCase = get()
        )
    }

    viewModel {
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