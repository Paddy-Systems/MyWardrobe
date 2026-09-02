package com.paddysystems.wearfolio.auth.network

import com.paddysystems.wearfolio.auth.session.SessionStore
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class AuthNetwork(
    baseUrl: String,
    sessions: SessionStore,
    val moshi: Moshi = createMoshi()
) {
    val publicApi: WearfolioAuthApi

    val authenticatedApi: WearfolioAuthApi

    init {
        require(baseUrl.endsWith('/')) {
            "Wearfolio API base URL must end with /"
        }

        val publicClient = OkHttpClient.Builder().build()

        publicApi = createRetrofit(
            baseUrl = baseUrl,
            client = publicClient,
            moshi = moshi
        ).create(WearfolioAuthApi::class.java)

        val authenticatedClient =
            OkHttpClient
                .Builder()
                .addInterceptor(
                    AccessTokenInterceptor(sessions)
                )
                .authenticator(
                    TokenRefreshAuthenticator(
                        sessions = sessions,
                        refreshApi = publicApi
                    )
                )
                .build()

        authenticatedApi = createRetrofit(
            baseUrl = baseUrl,
            client = authenticatedClient,
            moshi = moshi
        ).create(WearfolioAuthApi::class.java)
    }

    private fun createRetrofit(
        baseUrl: String,
        client: OkHttpClient,
        moshi: Moshi
    ): Retrofit {
        return Retrofit
            .Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(
                MoshiConverterFactory.create(moshi)
            )
            .build()
    }

    companion object {
        fun createMoshi(): Moshi {
            return Moshi
                .Builder()
                .addLast(KotlinJsonAdapterFactory())
                .build()
        }
    }
}