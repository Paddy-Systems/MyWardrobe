package com.paddysystems.wearfolio.auth.session

import android.content.Context
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SessionStore(
    context: Context,
    moshi: Moshi,
    private val cipher: SessionCipher
) {
    private val preferences =
        context.applicationContext.getSharedPreferences(
            PREFERENCES_NAME,
            Context.MODE_PRIVATE
        )

    private val adapter: JsonAdapter<StoredSession> =
        moshi.adapter(StoredSession::class.java)

    private val mutableSession =
        MutableStateFlow(readStoredSession())

    val session: StateFlow<StoredSession?> =
        mutableSession.asStateFlow()

    @Synchronized
    fun current(): StoredSession? {
        return mutableSession.value
    }

    @Synchronized
    fun save(session: StoredSession) {
        val json = adapter.toJson(session)
        val encrypted = cipher.encrypt(json)

        val saved = preferences
            .edit()
            .putString(SESSION_KEY, encrypted)
            .commit()

        check(saved) {
            "Failed to persist Wearfolio session"
        }

        mutableSession.value = session
    }

    @Synchronized
    fun clear() {
        preferences
            .edit()
            .remove(SESSION_KEY)
            .commit()

        mutableSession.value = null
    }

    private fun readStoredSession(): StoredSession? {
        val encrypted =
            preferences.getString(SESSION_KEY, null)
                ?: return null

        return try {
            val json = cipher.decrypt(encrypted)

            adapter.fromJson(json)
        } catch (_: Exception) {
            preferences
                .edit()
                .remove(SESSION_KEY)
                .commit()

            null
        }
    }

    private companion object {
        const val PREFERENCES_NAME = "wearfolio_auth"
        const val SESSION_KEY = "encrypted_session"
    }
}