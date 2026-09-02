package com.paddysystems.wearfolio.auth.session

import com.paddysystems.wearfolio.auth.network.AuthResponseDto
import com.paddysystems.wearfolio.auth.network.UserDto

data class StoredUser(
    val id: String,
    val email: String,
    val displayName: String,
    val emailVerifiedAt: String?,
    val createdAt: String
)

data class StoredSession(
    val user: StoredUser,
    val accessToken: String,
    val accessTokenExpiresIn: Long,
    val refreshToken: String,
    val refreshTokenExpiresAt: String
)

fun UserDto.toStoredUser(): StoredUser {
    return StoredUser(
        id = id,
        email = email,
        displayName = displayName,
        emailVerifiedAt = emailVerifiedAt,
        createdAt = createdAt
    )
}

fun AuthResponseDto.toStoredSession(): StoredSession {
    return StoredSession(
        user = user.toStoredUser(),
        accessToken = accessToken,
        accessTokenExpiresIn = accessTokenExpiresIn,
        refreshToken = refreshToken,
        refreshTokenExpiresAt = refreshTokenExpiresAt
    )
}