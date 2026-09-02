package com.paddysystems.mywardrobe.ui

import androidx.compose.runtime.staticCompositionLocalOf
import com.paddysystems.mywardrobe.data.model.Profile

val LocalActiveProfile = staticCompositionLocalOf<Profile> {
    error("No active profile provided")
}
