package com.paddysystems.wearfolio.auth.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface WearfolioAuthApi {
    @POST("v1/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): AuthResponseDto

    @POST("v1/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): AuthResponseDto

    @POST("v1/auth/google/nonce")
    suspend fun createGoogleNonce(): GoogleNonceDto

    @POST("v1/auth/google")
    suspend fun googleLogin(
        @Body request: GoogleLoginRequest
    ): AuthResponseDto

    @POST("v1/auth/refresh")
    suspend fun refresh(
        @Body request: RefreshRequest
    ): Response<AuthResponseDto>

    @POST("v1/auth/logout")
    suspend fun logout(): Response<Unit>

    @GET("v1/me")
    suspend fun me(): UserDto
}