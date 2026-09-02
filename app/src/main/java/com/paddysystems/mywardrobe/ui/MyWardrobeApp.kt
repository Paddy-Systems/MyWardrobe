package com.paddysystems.mywardrobe.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.paddysystems.mywardrobe.data.storage.ProfileStorage
import com.paddysystems.mywardrobe.ui.navigation.MyWardrobeNavigation
import com.paddysystems.mywardrobe.ui.screens.onboarding.OnboardingScreen

@Composable
fun MyWardrobeApp() {
    val context = LocalContext.current
    var activeProfile by remember { mutableStateOf(ProfileStorage.loadActiveProfile(context)) }

    val profile = activeProfile
    if (profile == null) {
        OnboardingScreen(onProfileCreated = { activeProfile = it })
    } else {
        CompositionLocalProvider(LocalActiveProfile provides profile) {
            MyWardrobeNavigation(onSwitchProfile = { activeProfile = it })
        }
    }
}
