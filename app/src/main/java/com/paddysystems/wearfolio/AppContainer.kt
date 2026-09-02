package com.paddysystems.wearfolio

import android.content.Context
import com.paddysystems.wearfolio.auth.google.GoogleCredentialProvider
import com.paddysystems.wearfolio.auth.network.AuthNetwork
import com.paddysystems.wearfolio.auth.repository.AuthRepository
import com.paddysystems.wearfolio.auth.repository.DefaultAuthRepository
import com.paddysystems.wearfolio.auth.session.SessionCipher
import com.paddysystems.wearfolio.auth.session.SessionStore

class AppContainer(
    context: Context
) {
    private val applicationContext =
        context.applicationContext

    private val moshi =
        AuthNetwork.createMoshi()

    val sessionStore = SessionStore(
        context = applicationContext,
        moshi = moshi,
        cipher = SessionCipher()
    )

    private val authNetwork = AuthNetwork(
        baseUrl = BuildConfig.API_BASE_URL,
        sessions = sessionStore,
        moshi = moshi
    )

    val authRepository: AuthRepository =
        DefaultAuthRepository(
            publicApi = authNetwork.publicApi,
            authenticatedApi =
                authNetwork.authenticatedApi,
            sessionStore = sessionStore,
            googleCredentialProvider =
                GoogleCredentialProvider.create(
                    applicationContext
                )
        )
}