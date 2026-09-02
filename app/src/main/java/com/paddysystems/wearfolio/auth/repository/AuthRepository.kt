package com.paddysystems.wearfolio.auth.repository

import android.app.Activity
import android.os.Build
import com.paddysystems.wearfolio.auth.google.GoogleCredentialProvider
import com.paddysystems.wearfolio.auth.network.GoogleLoginRequest
import com.paddysystems.wearfolio.auth.network.LoginRequest
import com.paddysystems.wearfolio.auth.network.RegisterRequest
import com.paddysystems.wearfolio.auth.network.WearfolioAuthApi
import com.paddysystems.wearfolio.auth.session.SessionStore
import com.paddysystems.wearfolio.auth.session.StoredSession
import com.paddysystems.wearfolio.auth.session.toStoredSession
import com.paddysystems.wearfolio.auth.session.toStoredUser
import java.util.concurrent.CancellationException
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val session: StateFlow<StoredSession?>

    suspend fun register(
        email: String,
        password: String,
        displayName: String
    )

    suspend fun login(
        email: String,
        password: String
    )

    suspend fun signInWithGoogle(
        activity: Activity
    )

    suspend fun refreshAccount()

    suspend fun logout()
}

class DefaultAuthRepository(
    private val publicApi: WearfolioAuthApi,
    private val authenticatedApi: WearfolioAuthApi,
    private val sessionStore: SessionStore,
    private val googleCredentialProvider:
        GoogleCredentialProvider
) : AuthRepository {
    override val session: StateFlow<StoredSession?>
        get() = sessionStore.session

    override suspend fun register(
        email: String,
        password: String,
        displayName: String
    ) {
        val response = publicApi.register(
            RegisterRequest(
                email = email.trim(),
                password = password,
                displayName = displayName.trim(),
                deviceName = deviceName()
            )
        )

        sessionStore.save(
            response.toStoredSession()
        )
    }

    override suspend fun login(
        email: String,
        password: String
    ) {
        val response = publicApi.login(
            LoginRequest(
                email = email.trim(),
                password = password,
                deviceName = deviceName()
            )
        )

        sessionStore.save(
            response.toStoredSession()
        )
    }

    override suspend fun signInWithGoogle(
        activity: Activity
    ) {
        val nonce = publicApi.createGoogleNonce()

        val idToken =
            googleCredentialProvider
                .getButtonIdToken(
                    activity = activity,
                    nonce = nonce.nonce
                )

        val response = publicApi.googleLogin(
            GoogleLoginRequest(
                idToken = idToken,
                nonceId = nonce.id,
                deviceName = deviceName()
            )
        )

        sessionStore.save(
            response.toStoredSession()
        )
    }

    override suspend fun refreshAccount() {
        if (sessionStore.current() == null) {
            return
        }

        val user = authenticatedApi.me()

        /*
         * Read the session after the request. The authenticator
         * may have refreshed and replaced the tokens while the
         * /me request was running.
         */
        val latestSession =
            sessionStore.current()
                ?: return

        sessionStore.save(
            latestSession.copy(
                user = user.toStoredUser()
            )
        )
    }

    override suspend fun logout() {
        try {
            authenticatedApi.logout()
        } catch (error: CancellationException) {
            throw error
        } catch (_: Exception) {
            /*
             * An unavailable server must not prevent the user
             * from clearing the local session.
             */
        } finally {
            sessionStore.clear()
        }

        try {
            googleCredentialProvider
                .clearCredentialState()
        } catch (error: CancellationException) {
            throw error
        } catch (_: Exception) {
            /*
             * Credential Manager cleanup failing does not restore
             * the Wearfolio session.
             */
        }
    }

    private fun deviceName(): String {
        return "${Build.MANUFACTURER} ${Build.MODEL}"
            .trim()
            .take(120)
    }
}