package com.webscare.interiorismai.ui.common.base

sealed class CommonUiEvent {
    data class ShowError(val message: String) : CommonUiEvent()
    data class ShowSuccess(val message: String) : CommonUiEvent()
    object NavigateToSuccess : CommonUiEvent()
    object NavigateAfterLogout : CommonUiEvent()
    object NavigateToHome : CommonUiEvent()
}