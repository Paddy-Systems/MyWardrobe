package com.paddysystems.wearfolio.auth.session

import com.paddysystems.wearfolio.auth.network.AuthResponseDto
import com.paddysystems.wearfolio.auth.network.UserDto
import org.junit.Assert.assertEquals
import org.junit.Test

class StoredSessionTest {
    @Test
    fun convertsApiResponseToStoredSession() {
        val response = AuthResponseDto(
            user =
                UserDto(
                    id = "user-id",
                    email = "paddy@example.com",
                    displayName = "Paddy",
                    emailVerifiedAt = null,
                    createdAt = "2026-09-02T12:00:00.000Z"
                ),
            accessToken = "access-token",
            accessTokenExpiresIn = 900,
            refreshToken = "refresh-token",
            refreshTokenExpiresAt =
                "2026-10-02T12:00:00.000Z"
        )

        val stored = response.toStoredSession()

        assertEquals("user-id", stored.user.id)
        assertEquals("access-token", stored.accessToken)
        assertEquals("refresh-token", stored.refreshToken)
        assertEquals(900, stored.accessTokenExpiresIn)
    }
}