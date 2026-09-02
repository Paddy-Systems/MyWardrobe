package com.paddysystems.wearfolio.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.paddysystems.wearfolio.WearfolioApplication
import com.paddysystems.wearfolio.data.backup.WardrobeBackupService
import com.paddysystems.wearfolio.data.storage.ProfileStorage
import com.paddysystems.wearfolio.ui.navigation.WearfolioNavigation
import com.paddysystems.wearfolio.ui.screens.account.AccountRoute
import com.paddysystems.wearfolio.ui.screens.onboarding.OnboardingScreen

@Composable
fun WearfolioApp() {
    val context = LocalContext.current
    val application =
        context.applicationContext as WearfolioApplication

    val session by
        application
            .appContainer
            .sessionStore
            .session
            .collectAsStateWithLifecycle()

    val initialProfile = remember(context) {
        WardrobeBackupService
            .recoverInterruptedRestore(
                context.applicationContext
            )

        ProfileStorage.loadActiveProfile(context)
            ?: ProfileStorage
                .loadProfiles(context)
                .firstOrNull()
                ?.also { profile ->
                    ProfileStorage.setActiveProfile(
                        context = context,
                        profileId = profile.id
                    )
                }
    }

    var activeProfile by remember {
        mutableStateOf(
            initialProfile
        )
    }

    when {
        session == null -> {
            AccountRoute()
        }

        activeProfile == null -> {
            OnboardingScreen(
                onProfileCreated = {
                    activeProfile = it
                }
            )
        }

        else -> {
            CompositionLocalProvider(
                LocalActiveProfile provides
                    requireNotNull(activeProfile)
            ) {
                WearfolioNavigation(
                    onSwitchProfile = {
                        activeProfile = it
                    }
                )
            }
        }
    }
}
