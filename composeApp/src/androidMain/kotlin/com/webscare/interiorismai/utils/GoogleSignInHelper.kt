package com.webscare.interiorismai.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException

actual class GoogleSignInHelper(private val context: Context) {

    private val credentialManager = CredentialManager.create(context)

    actual suspend fun signIn(): GoogleSignInResult {
        val activityContext = context.findActivity() ?: return GoogleSignInResult(
            error = "Activity context not found. Call from Activity only."
        )
        return try {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId("64265820531-0af96u1dad5gqd94astrb6268h2908ft.apps.googleusercontent.com")
                .setAutoSelectEnabled(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(
                request = request,
                context = activityContext
            )

            // handleGoogleCredential logic
            val credential = result.credential
            if (credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                try {
                    val googleCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    val email = googleCredential.id
                    Log.d("GoogleSignIn", "Success google email: $email")
                    GoogleSignInResult(email = email)

                } catch (e: GoogleIdTokenParsingException) {
                    Log.e("GoogleSignIn", "Token parsing failed: ${e.message}")
                    GoogleSignInResult(error = "Token parsing failed: ${e.message}")
                }
            } else {
                Log.w("GoogleSignIn", "Unexpected credential type: ${credential.type}")
                GoogleSignInResult(error = "Unexpected credential type")
            }

        } catch (e: GetCredentialException) {
            Log.e("GoogleSignIn", "Failed: ${e.message}")
            GoogleSignInResult(error = e.message)
        }
    }
}
fun Context.findActivity(): Activity? {
    var ctx = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    return null
}