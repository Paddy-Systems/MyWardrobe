package com.paddysystems.wearfolio.ui.screens.account

import android.app.Activity
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.paddysystems.wearfolio.auth.repository.AuthRepository
import com.paddysystems.wearfolio.auth.session.StoredSession
import java.io.IOException
import java.util.concurrent.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException

enum class AccountMode {
    LOGIN,
    REGISTER
}

data class AccountUiState(
    val session: StoredSession? = null,
    val mode: AccountMode = AccountMode.LOGIN,
    val email: String = "",
    val password: String = "",
    val displayName: String = "",
    val isBusy: Boolean = false,
    val errorMessage: String? = null
)

class AccountViewModel(
    private val repository: AuthRepository
) : ViewModel() {
    private val mutableUiState =
        MutableStateFlow(
            AccountUiState(
                session = repository.session.value
            )
        )

    val uiState: StateFlow<AccountUiState> =
        mutableUiState.asStateFlow()

    init {
        observeSession()

        if (repository.session.value != null) {
            runAction {
                repository.refreshAccount()
            }
        }
    }

    fun setMode(mode: AccountMode) {
        mutableUiState.update {
            it.copy(
                mode = mode,
                errorMessage = null
            )
        }
    }

    fun setEmail(email: String) {
        mutableUiState.update {
            it.copy(
                email = email,
                errorMessage = null
            )
        }
    }

    fun setPassword(password: String) {
        mutableUiState.update {
            it.copy(
                password = password,
                errorMessage = null
            )
        }
    }

    fun setDisplayName(displayName: String) {
        mutableUiState.update {
            it.copy(
                displayName = displayName,
                errorMessage = null
            )
        }
    }

    fun submit() {
        val state = mutableUiState.value

        val validationError =
            validate(state)

        if (validationError != null) {
            showError(validationError)
            return
        }

        runAction {
            when (state.mode) {
                AccountMode.LOGIN ->
                    repository.login(
                        email = state.email,
                        password = state.password
                    )

                AccountMode.REGISTER ->
                    repository.register(
                        email = state.email,
                        password = state.password,
                        displayName =
                            state.displayName
                    )
            }
        }
    }

    fun signInWithGoogle(activity: Activity) {
        runAction {
            repository.signInWithGoogle(
                activity
            )
        }
    }

    fun logout() {
        runAction {
            repository.logout()
        }
    }

    private fun observeSession() {
        viewModelScope.launch {
            repository.session.collect { session ->
                mutableUiState.update { state ->
                    state.copy(
                        session = session,
                        password =
                            if (session != null) {
                                ""
                            } else {
                                state.password
                            }
                    )
                }
            }
        }
    }

    private fun validate(
        state: AccountUiState
    ): String? {
        val email = state.email.trim()
        val displayName =
            state.displayName.trim()

        if (email.isEmpty()) {
            return "Enter your email address."
        }

        if (email.length > 320) {
            return "Your email address is too long."
        }

        if (
            !email.contains("@") ||
            email.startsWith("@") ||
            email.endsWith("@")
        ) {
            return "Enter a valid email address."
        }

        if (state.password.isEmpty()) {
            return "Enter your password."
        }

        if (state.password.length > 128) {
            return "Your password is too long."
        }

        if (
            state.mode == AccountMode.REGISTER &&
            state.password.length < 12
        ) {
            return "Your password must contain at least 12 characters."
        }

        if (
            state.mode == AccountMode.REGISTER &&
            displayName.isEmpty()
        ) {
            return "Enter a display name."
        }

        if (
            state.mode == AccountMode.REGISTER &&
            displayName.length > 100
        ) {
            return "Your display name must contain 100 characters or fewer."
        }

        return null
    }

    private fun runAction(
        action: suspend () -> Unit
    ) {
        if (mutableUiState.value.isBusy) {
            return
        }

        viewModelScope.launch {
            mutableUiState.update {
                it.copy(
                    isBusy = true,
                    errorMessage = null
                )
            }

            try {
                action()
            } catch (
                _: GetCredentialCancellationException
            ) {
                /*
                 * Closing Google's account selector is a normal
                 * user action, so do not display an error.
                 */
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                showError(
                    error.toUserMessage()
                )
            } finally {
                mutableUiState.update {
                    it.copy(isBusy = false)
                }
            }
        }
    }

    private fun showError(message: String) {
        mutableUiState.update {
            it.copy(errorMessage = message)
        }
    }

    private fun Throwable.toUserMessage(): String {
        return when (this) {
            is IOException ->
                "Wearfolio could not reach the server. Check your connection and try again."

            is HttpException ->
                when (code()) {
                    400 ->
                        "Check the information you entered and try again."

                    401 ->
                        "The email, password, or sign-in credential was not accepted."

                    409 ->
                        "An account already exists for that email address."

                    429 ->
                        "Too many attempts. Wait a moment and try again."

                    in 500..599 ->
                        "The Wearfolio server is temporarily unavailable."

                    else ->
                        "The request could not be completed."
                }

            else ->
                "Something went wrong. Please try again."
        }
    }

    class Factory(
        private val repository: AuthRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(
            modelClass: Class<T>
        ): T {
            if (
                modelClass.isAssignableFrom(
                    AccountViewModel::class.java
                )
            ) {
                return AccountViewModel(
                    repository
                ) as T
            }

            throw IllegalArgumentException(
                "Unknown ViewModel class: ${modelClass.name}"
            )
        }
    }
}