package com.webscare.interiorismai.navigation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
sealed class Routes {

    @Serializable
    @SerialName("onboarding")
    data object OnBoarding : Routes()

    @Serializable
    @SerialName("register")
    data object Register : Routes()

    @Serializable
    @SerialName("verification")
    data object Verification : Routes()

    @Serializable
    @SerialName("welcome")
    data object Welcome : Routes()

    @Serializable
    @SerialName("login")
    data object Login : Routes()

    @Serializable
    @SerialName("baseappscreen")
    data object BaseAppScreen : Routes()

    @Serializable
    @SerialName("splash")
    data object Splash : Routes()

    @Serializable
    @SerialName("add")
    data object AddScreen : Routes()

    @Serializable
    @SerialName("Create")
    data object Create : Routes()

    @Serializable
    @SerialName("Explore")
    data object Explore : Routes()

    @Serializable
    @SerialName("Files")
    data object Files : Routes()

    @Serializable
    @SerialName("file-edit")
    data class FileEdit(
        val imageUrl: String = "" ,
        val imageIndex: Int = -1,
        val entityId: Long = -1L
    ) : Routes()

    @Serializable
    @SerialName("generate")
    data object AbtToGenerate : Routes()

    @Serializable
    @SerialName("Account")
    data object Account : Routes()

    @Serializable
    @SerialName("subscription")
    data object Subscription : Routes()

    @Serializable
    @SerialName("result")
    data object Result : Routes()

    @Serializable
    @SerialName("profile")
    data object Profile : Routes()

    @Serializable
    @SerialName("forgetEmail")
    data object ForgetEmail : Routes()

    @Serializable
    @SerialName("forgetOtp")
    data object ForgetOTP : Routes()

    @Serializable
    @SerialName("forgetNewPass")
    data object ForgetNewPass : Routes()

    @Serializable
    data class CreateEdit(val imageUrl: String) : Routes()
    @Serializable
    @SerialName("generate_with_data")
    data class AbtToGenerateWithData(
        val imageUrl: String,
        val style: String,
        val type: String,
        val colors: List<String>
    ) : Routes()
}
