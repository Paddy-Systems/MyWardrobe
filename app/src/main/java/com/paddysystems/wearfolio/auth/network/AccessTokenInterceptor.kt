package com.paddysystems.wearfolio.auth.network

import com.paddysystems.wearfolio.auth.session.SessionStore
import okhttp3.Interceptor
import okhttp3.Response

class AccessTokenInterceptor(
    private val sessions: SessionStore
) : Interceptor {
    override fun intercept(
        chain: Interceptor.Chain
    ): Response {
        val session = sessions.current()

        if (
            session == null ||
            chain.request().header("Authorization") != null
        ) {
            return chain.proceed(chain.request())
        }

        val authenticatedRequest =
            chain.request()
                .newBuilder()
                .header(
                    "Authorization",
                    "Bearer ${session.accessToken}"
                )
                .build()

        return chain.proceed(authenticatedRequest)
    }
}