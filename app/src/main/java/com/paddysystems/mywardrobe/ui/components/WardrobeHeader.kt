package com.paddysystems.mywardrobe.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun WardrobeHeader(
    profileName: String,
    itemCount: Int,
    totalItemCount: Int,
    selectedCount: Int,
    onManageWardrobes: () -> Unit,
    modifier: Modifier = Modifier
) {
    val subtitle = if (itemCount == totalItemCount) {
        "$totalItemCount ${itemWord(totalItemCount)}, ready to style"
    } else {
        "$itemCount of $totalItemCount ${itemWord(totalItemCount)} shown"
    }
    EditorialPageHeader(
        eyebrow = if (selectedCount > 0) "Editing wardrobe" else "Wearfolio · Your collection",
        title = if (selectedCount > 0) "$selectedCount selected" else "$profileName's Wardrobe",
        subtitle = if (selectedCount > 0) "Choose what to do with these pieces" else subtitle,
        actionIcon = if (selectedCount > 0) null else Icons.Outlined.Groups,
        onAction = if (selectedCount > 0) null else onManageWardrobes,
        modifier = modifier
    )
}

private fun itemWord(
    count: Int
): String {
    return if (count == 1) {
        "item"
    } else {
        "items"
    }
}
