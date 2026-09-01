package com.paddysystems.mywardrobe.ui.screens.wardrobe

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
fun rememberWardrobeScreenState(context: Context): WardrobeScreenState =
    remember(context) { WardrobeScreenState(context) }
