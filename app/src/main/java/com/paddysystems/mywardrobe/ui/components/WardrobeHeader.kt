package com.paddysystems.mywardrobe.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun WardrobeHeader(
    itemCount: Int,
    totalItemCount: Int,
    selectedCount: Int,
    modifier: Modifier = Modifier
) {
    val subtitle = if (itemCount == totalItemCount) {
        "$totalItemCount ${if (totalItemCount == 1) "item" else "items"}, ready to style"
    } else {
        "$itemCount of $totalItemCount ${if (totalItemCount == 1) "item" else "items"} shown"
    }
    EditorialPageHeader(
        eyebrow = if (selectedCount > 0) "Editing wardrobe" else "Your collection",
        title = if (selectedCount > 0) "$selectedCount selected" else "My Wardrobe",
        subtitle = if (selectedCount > 0) "Choose what to do with these pieces" else subtitle,
        modifier = modifier
    )
}
