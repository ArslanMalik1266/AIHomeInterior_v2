package com.webscare.interiorismai.domain.model

data class AboutToGenerateUiState(
    val selectedType: String = "Bedroom",
    val selectedStyle: String = "Cont",
    val selectedImageRes: String? = null,
    val isLoading: Boolean = false
)