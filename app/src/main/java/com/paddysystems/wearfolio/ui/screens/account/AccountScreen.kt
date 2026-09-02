package com.paddysystems.wearfolio.ui.screens.account

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.paddysystems.wearfolio.WearfolioApplication
import com.paddysystems.wearfolio.ui.components.EditorialDangerButton
import com.paddysystems.wearfolio.ui.components.EditorialPageHeader
import com.paddysystems.wearfolio.ui.components.EditorialPrimaryButton
import com.paddysystems.wearfolio.ui.components.EditorialSecondaryButton

@Composable
fun AccountRoute(
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val application =
        context.applicationContext as WearfolioApplication

    val activity = remember(context) {
        context.findActivity()
    }

    val accountViewModel: AccountViewModel =
        viewModel(
            factory =
                AccountViewModel.Factory(
                    repository =
                        application
                            .appContainer
                            .authRepository
                )
        )

    val state by
        accountViewModel
            .uiState
            .collectAsStateWithLifecycle()

    AccountScreen(
        state = state,
        onBack = onBack,
        onModeChanged =
            accountViewModel::setMode,
        onEmailChanged =
            accountViewModel::setEmail,
        onPasswordChanged =
            accountViewModel::setPassword,
        onDisplayNameChanged =
            accountViewModel::setDisplayName,
        onSubmit =
            accountViewModel::submit,
        onGoogleSignIn = {
            activity?.let {
                accountViewModel
                    .signInWithGoogle(it)
            }
        },
        googleSignInEnabled =
            activity != null &&
                !state.isBusy,
        onLogout =
            accountViewModel::logout,
        modifier = modifier
    )
}

@Composable
private fun AccountScreen(
    state: AccountUiState,
    onBack: (() -> Unit)?,
    onModeChanged: (AccountMode) -> Unit,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onDisplayNameChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    onGoogleSignIn: () -> Unit,
    googleSignInEnabled: Boolean,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(
                    MaterialTheme
                        .colorScheme
                        .background
                )
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(
                    horizontal = 20.dp,
                    vertical = 22.dp
                ),
        verticalArrangement =
            Arrangement.spacedBy(16.dp)
    ) {
        EditorialPageHeader(
            eyebrow = "Account",
            title = "Wearfolio account",
            subtitle =
                if (state.session == null) {
                    "Sign in to connect this device to your Wearfolio account."
                } else {
                    "Manage the account connected to this device."
                },
            navigationIcon =
                if (onBack != null) {
                    Icons.AutoMirrored
                        .Outlined
                        .ArrowBack
                } else {
                    null
                },
            onNavigate = onBack
        )

        if (state.session == null) {
            SignedOutContent(
                state = state,
                onModeChanged = onModeChanged,
                onEmailChanged = onEmailChanged,
                onPasswordChanged =
                    onPasswordChanged,
                onDisplayNameChanged =
                    onDisplayNameChanged,
                onSubmit = onSubmit,
                onGoogleSignIn =
                    onGoogleSignIn,
                googleSignInEnabled =
                    googleSignInEnabled
            )
        } else {
            SignedInContent(
                state = state,
                onLogout = onLogout
            )
        }
    }
}

@Composable
private fun SignedOutContent(
    state: AccountUiState,
    onModeChanged: (AccountMode) -> Unit,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onDisplayNameChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    onGoogleSignIn: () -> Unit,
    googleSignInEnabled: Boolean
) {
    Text(
        text =
            "Your wardrobes remain on this device when you sign out.",
        style =
            MaterialTheme.typography.bodyMedium,
        color =
            MaterialTheme
                .colorScheme
                .onSurfaceVariant
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement =
            Arrangement.spacedBy(12.dp)
    ) {
        FilterChip(
            selected =
                state.mode ==
                    AccountMode.LOGIN,
            onClick = {
                onModeChanged(
                    AccountMode.LOGIN
                )
            },
            label = {
                Text("Sign in")
            },
            enabled = !state.isBusy
        )

        FilterChip(
            selected =
                state.mode ==
                    AccountMode.REGISTER,
            onClick = {
                onModeChanged(
                    AccountMode.REGISTER
                )
            },
            label = {
                Text("Create account")
            },
            enabled = !state.isBusy
        )
    }

    if (
        state.mode ==
        AccountMode.REGISTER
    ) {
        OutlinedTextField(
            value = state.displayName,
            onValueChange =
                onDisplayNameChanged,
            modifier =
                Modifier.fillMaxWidth(),
            label = {
                Text("Display name")
            },
            singleLine = true,
            enabled = !state.isBusy
        )
    }

    OutlinedTextField(
        value = state.email,
        onValueChange = onEmailChanged,
        modifier = Modifier.fillMaxWidth(),
        label = {
            Text("Email address")
        },
        keyboardOptions =
            KeyboardOptions(
                keyboardType =
                    KeyboardType.Email
            ),
        singleLine = true,
        enabled = !state.isBusy
    )

    OutlinedTextField(
        value = state.password,
        onValueChange =
            onPasswordChanged,
        modifier = Modifier.fillMaxWidth(),
        label = {
            Text("Password")
        },
        visualTransformation =
            PasswordVisualTransformation(),
        keyboardOptions =
            KeyboardOptions(
                keyboardType =
                    KeyboardType.Password
            ),
        singleLine = true,
        enabled = !state.isBusy
    )

    ErrorMessage(
        message = state.errorMessage
    )

    if (state.isBusy) {
        LoadingIndicator()
    }

    EditorialPrimaryButton(
        text =
            when (state.mode) {
                AccountMode.LOGIN ->
                    "Sign in"

                AccountMode.REGISTER ->
                    "Create account"
            },
        onClick = onSubmit,
        modifier = Modifier.fillMaxWidth(),
        enabled = !state.isBusy
    )

    EditorialSecondaryButton(
        text = "Continue with Google",
        onClick = onGoogleSignIn,
        modifier = Modifier.fillMaxWidth(),
        enabled = googleSignInEnabled
    )
}

@Composable
private fun SignedInContent(
    state: AccountUiState,
    onLogout: () -> Unit
) {
    val user =
        requireNotNull(state.session)
            .user

    Text(
        text = user.displayName,
        style =
            MaterialTheme
                .typography
                .headlineSmall,
        color =
            MaterialTheme
                .colorScheme
                .onBackground
    )

    Text(
        text = user.email,
        style =
            MaterialTheme
                .typography
                .bodyLarge,
        color =
            MaterialTheme
                .colorScheme
                .onBackground
    )

    Text(
        text =
            if (
                user.emailVerifiedAt != null
            ) {
                "Email verified"
            } else {
                "Email not yet verified"
            },
        style =
            MaterialTheme
                .typography
                .bodyMedium,
        color =
            MaterialTheme
                .colorScheme
                .onSurfaceVariant
    )

    ErrorMessage(
        message = state.errorMessage
    )

    if (state.isBusy) {
        LoadingIndicator()
    }

    Spacer(
        modifier = Modifier.height(4.dp)
    )

    Text(
        text =
            "Signing out does not remove wardrobes or clothing saved on this device.",
        style =
            MaterialTheme
                .typography
                .bodyMedium,
        color =
            MaterialTheme
                .colorScheme
                .onSurfaceVariant
    )

    EditorialDangerButton(
        text = "Sign out",
        onClick = onLogout,
        modifier = Modifier.fillMaxWidth(),
        enabled = !state.isBusy
    )
}

@Composable
private fun ErrorMessage(
    message: String?
) {
    if (message == null) {
        return
    }

    Text(
        text = message,
        style =
            MaterialTheme
                .typography
                .bodyMedium,
        color =
            MaterialTheme
                .colorScheme
                .error
    )
}

@Composable
private fun LoadingIndicator() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement =
            Arrangement.Center,
        verticalAlignment =
            Alignment.CenterVertically
    ) {
        CircularProgressIndicator()
    }
}

private tailrec fun Context.findActivity(): Activity? {
    return when (this) {
        is Activity ->
            this

        is ContextWrapper ->
            baseContext.findActivity()

        else ->
            null
    }
}
