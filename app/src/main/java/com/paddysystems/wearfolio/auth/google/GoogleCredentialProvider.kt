package com.paddysystems.wearfolio.auth.google

import android.app.Activity
import android.content.Context
import android.content.MutableContextWrapper
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential

class GoogleCredentialProvider private constructor(
    private val credentialManager: CredentialManager,
    private val serverClientId: String
) {
    suspend fun getAuthorizedAccountIdToken(
        activity: Activity,
        nonce: String
    ): String {
        requireConfigured()
        require(nonce.isNotBlank()) {
            "Google nonce cannot be blank"
        }

        return try {
            requestBottomSheetToken(
                activity = activity,
                nonce = nonce,
                authorizedAccountsOnly = true
            )
        } catch (_: NoCredentialException) {
            requestBottomSheetToken(
                activity = activity,
                nonce = nonce,
                authorizedAccountsOnly = false
            )
        }
    }

    suspend fun getButtonIdToken(
        activity: Activity,
        nonce: String
    ): String {
        requireConfigured()
        require(nonce.isNotBlank()) {
            "Google nonce cannot be blank"
        }

        val option = GetSignInWithGoogleOption.Builder(
            serverClientId = serverClientId
        )
            .setNonce(nonce)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(option)
            .build()

        val response = credentialManager.getCredential(
            context = MutableContextWrapper(activity),
            request = request
        )

        return extractIdToken(response)
    }

    suspend fun clearCredentialState() {
        credentialManager.clearCredentialState(
            ClearCredentialStateRequest()
        )
    }

    private suspend fun requestBottomSheetToken(
        activity: Activity,
        nonce: String,
        authorizedAccountsOnly: Boolean
    ): String {
        val option = GetGoogleIdOption.Builder()
            .setServerClientId(serverClientId)
            .setNonce(nonce)
            .setFilterByAuthorizedAccounts(
                authorizedAccountsOnly
            )
            .setAutoSelectEnabled(
                authorizedAccountsOnly
            )
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(option)
            .build()

        val response = credentialManager.getCredential(
            context = MutableContextWrapper(activity),
            request = request
        )

        return extractIdToken(response)
    }

    private fun extractIdToken(
        response: GetCredentialResponse
    ): String {
        val credential = response.credential

        if (
            credential !is CustomCredential ||
            credential.type !=
            GoogleIdTokenCredential
                .TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            throw UnexpectedGoogleCredentialException()
        }

        return GoogleIdTokenCredential
            .createFrom(credential.data)
            .idToken
    }

    private fun requireConfigured() {
        check(serverClientId.isNotBlank()) {
            "GOOGLE_WEB_CLIENT_ID is not configured"
        }
    }

    companion object {
        fun create(context: Context): GoogleCredentialProvider {
            return GoogleCredentialProvider(
                credentialManager = CredentialManager.create(
                    context.applicationContext
                ),
                serverClientId =
                    com.paddysystems.wearfolio
                        .BuildConfig
                        .GOOGLE_WEB_CLIENT_ID
            )
        }
    }
}

class UnexpectedGoogleCredentialException :
    IllegalStateException(
        "Credential Manager returned an unexpected credential"
    )