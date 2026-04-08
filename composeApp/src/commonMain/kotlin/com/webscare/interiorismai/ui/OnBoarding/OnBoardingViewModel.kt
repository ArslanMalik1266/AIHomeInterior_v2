package com.webscare.interiorismai.ui.OnBoarding

import androidx.lifecycle.ViewModel
import com.russhwolf.settings.Settings
import com.webscare.interiorismai.utils.Constants

class OnBoardingViewModel(val settings: Settings) : ViewModel(){
    fun onBoardingDone() {
        settings.putBoolean(Constants.ONBOARDING, true)
    }
}