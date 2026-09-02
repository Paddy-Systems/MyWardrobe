package com.paddysystems.wearfolio.ui

import androidx.compose.runtime.staticCompositionLocalOf
import com.paddysystems.wearfolio.data.model.Profile

val LocalActiveProfile = staticCompositionLocalOf<Profile> {
    error("No active profile provided")
}
