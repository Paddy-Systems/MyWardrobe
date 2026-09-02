package com.paddysystems.wearfolio.auth.network

data class UserDto(
    val id: String,
    val email: String,
    val displayName: String,
    val emailVerifiedAt: String?,
    val createdAt: String
)

data class AuthResponseDto(
    val user: UserDto,
    val accessToken: String,
    val accessTokenExpiresIn: Long,
    val refreshToken: String,
    val refreshTokenExpiresAt: String
)

data class RegisterRequest(
    val email: String,
    val password: String,
    val displayName: String,
    val deviceName: String
)

data class LoginRequest(
    val email: String,
    val password: String,
    val deviceName: String
)

data class RefreshRequest(
    val refreshToken: String
)

data class GoogleNonceDto(
    val id: String,
    val nonce: String,
    val expiresAt: String
)

data class GoogleLoginRequest(
    val idToken: String,
    val nonceId: String,
    val deviceName: String
)