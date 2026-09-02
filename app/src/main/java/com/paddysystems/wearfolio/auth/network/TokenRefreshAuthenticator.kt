package com.paddysystems.wearfolio.auth.network

import com.paddysystems.wearfolio.auth.session.SessionStore
import com.paddysystems.wearfolio.auth.session.toStoredSession
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenRefreshAuthenticator(
    private val sessions: SessionStore,
    private val refreshApi: WearfolioAuthApi
) : Authenticator {
    private val refreshMutex = Mutex()

    override fun authenticate(
        route: Route?,
        response: Response
    ): Request? {
        if (responseCount(response) >= 2) {
            return null
        }

        val failedAuthorization =
            response.request.header("Authorization")
                ?: return null

        return runBlocking {
            refreshMutex.withLock {
                val current = sessions.current()
                    ?: return@withLock null

                val currentAuthorization =
                    "Bearer ${current.accessToken}"

                if (failedAuthorization != currentAuthorization) {
                    return@withLock response.request
                        .newBuilder()
                        .header(
                            "Authorization",
                            currentAuthorization
                        )
                        .build()
                }

                val refreshResponse = try {
                    refreshApi.refresh(
                        RefreshRequest(
                            refreshToken = current.refreshToken
                        )
                    )
                } catch (_: Exception) {
                    return@withLock null
                }

                if (refreshResponse.code() == 401) {
                    sessions.clear()
                    return@withLock null
                }

                if (!refreshResponse.isSuccessful) {
                    return@withLock null
                }

                val refreshed = refreshResponse.body()
                    ?: return@withLock null

                val stored = refreshed.toStoredSession()

                sessions.save(stored)

                response.request
                    .newBuilder()
                    .header(
                        "Authorization",
                        "Bearer ${stored.accessToken}"
                    )
                    .build()
            }
        }
    }

    private fun responseCount(response: Response): Int {
        var current: Response? = response
        var count = 1

        while (current?.priorResponse != null) {
            count++
            current = current.priorResponse
        }

        return count
    }
}