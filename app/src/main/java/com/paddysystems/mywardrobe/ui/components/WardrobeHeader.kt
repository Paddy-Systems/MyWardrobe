package com.paddysystems.mywardrobe.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun WardrobeHeader(
    itemCount: Int,
    selectedCount: Int,
    modifier: Modifier = Modifier
) {
    Text(
        text = if (selectedCount == 0) {
            "My Wardrobe · $itemCount items"
        } else {
            "$selectedCount selected"
        },
        style = MaterialTheme.typography.headlineLarge,
        modifier = modifier
    )
}