package com.webscare.interiorismai.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil3.SingletonImageLoader
import coil3.compose.LocalPlatformContext
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication
import org.koin.compose.viewmodel.koinViewModel
import org.koin.dsl.KoinAppDeclaration
import com.webscare.interiorismai.di.appModule
import com.webscare.interiorismai.di.platformModule
import com.webscare.interiorismai.navigation.Routes
import com.webscare.interiorismai.ui.authentication.AuthViewModel
import com.webscare.interiorismai.ui.authentication.ForgetPassword.ForgetEmailRoot
import com.webscare.interiorismai.ui.authentication.ForgetPassword.ForgetOTPRoot
import com.webscare.interiorismai.ui.authentication.ForgetPassword.NewPassRoot
import com.webscare.interiorismai.ui.authentication.Login.LoginRoot
import com.webscare.interiorismai.ui.authentication.Login.WelcomeScreen
import com.webscare.interiorismai.ui.BottomBarScreen.BaseBottomBarScreen
import com.webscare.interiorismai.ui.OnBoarding.BaseScreen
import com.webscare.interiorismai.ui.OnBoarding.OnBoardingViewModel
import com.webscare.interiorismai.ui.OnBoarding.SplashScreen
import com.webscare.interiorismai.ui.authentication.Verification.VerificationRoot
import com.webscare.interiorismai.ui.theme.AppTypography
import com.webscare.interiorismai.utils.createImageLoader

@Composable
@Preview
fun App(koinAppDeclaration: KoinAppDeclaration? = null) {

    val context = LocalPlatformContext.current
    SingletonImageLoader.setSafe {
        createImageLoader(context)
    }


    KoinApplication(application = {
        koinAppDeclaration?.invoke(this)
        modules(appModule() + platformModule())
    }) {
        val navController = rememberNavController()
        val authViewModel: AuthViewModel = koinViewModel()
        MaterialTheme(typography = AppTypography()) {
            NavHost(navController, startDestination = Routes.Splash) {
                composable<Routes.Welcome> {
                    WelcomeScreen(onLogin = {
                        navController.navigate(Routes.Login)
                    })
                }
                composable<Routes.Login> {
                    LoginRoot(
                        authViewModel = authViewModel,
                        navController = navController,
                        onBackClick = {
                            navController.popBackStack()
                        })

                }
                composable<Routes.Splash> {
                    SplashScreen(navController = navController,
                        authViewModel = authViewModel)
                }
                composable<Routes.ForgetEmail> {
                    ForgetEmailRoot(onBack = {
                        navController.navigateUp()
                    }, onSuccess = {
                        navController.navigate(Routes.ForgetOTP)
                    })
                }

                composable<Routes.ForgetOTP> {
                    val parent = remember(navController) {
                        navController.previousBackStackEntry
                    }
                    parent?.let {
                        val authViewModel: AuthViewModel = koinViewModel(viewModelStoreOwner = it)
                        ForgetOTPRoot(onBackClick = {
                            navController.navigateUp()
                        }, authViewModel = authViewModel, onSuccess = {
                            navController.navigate(Routes.ForgetNewPass)
                        })
                    }
                }

                composable<Routes.ForgetNewPass> {
                    val parent = remember(it) {
                        navController.getBackStackEntry(Routes.ForgetEmail)
                    }

                    val authViewModel: AuthViewModel = koinViewModel(viewModelStoreOwner = parent)
                    NewPassRoot(authViewModel, onBack = {
                        navController.navigateUp()
                    }, onSuccess = {
                        navController.navigate(Routes.Login) {
                            popUpTo(Routes.Login) {
                                inclusive = false
                            }
                        }
                    })

                }

                composable<Routes.Verification> {
                    VerificationRoot(
                        onBackClick = { navController.popBackStack() },
                        authViewModel = authViewModel,
                        onSuccess = {
                            navController.navigate(Routes.BaseAppScreen) {
                                popUpTo(0) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    )
                }
                composable<Routes.OnBoarding> {
                    val onBoardingViewModel: OnBoardingViewModel = koinViewModel()
                    BaseScreen() {
                        onBoardingViewModel.onBoardingDone()
                        navController.navigate(Routes.BaseAppScreen)
                    }
                }
                composable<Routes.BaseAppScreen> {
                    BaseBottomBarScreen(rootNavController = navController,
                        authViewModel = authViewModel)

                }
            }
        }
    }
}