package com.paddysystems.mywardrobe.ui.navigation

import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddAPhoto
import androidx.compose.material.icons.outlined.Checkroom
import androidx.compose.material.icons.outlined.Style
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

enum class MyWardrobeDestination { WARDROBE, ADD, OUTFITS }

@Composable
fun MyWardrobeBottomBar(
    selectedDestination: MyWardrobeDestination,
    onAddItem: () -> Unit,
    onViewOutfits: () -> Unit,
    onWardrobe: () -> Unit
) {
    val colors = NavigationBarItemDefaults.colors(
        selectedIconColor = MaterialTheme.colorScheme.primary,
        selectedTextColor = MaterialTheme.colorScheme.primary,
        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
    )
    NavigationBar(
        modifier = Modifier.height(82.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp
    ) {
        NavigationBarItem(
            selected = selectedDestination == MyWardrobeDestination.WARDROBE,
            onClick = onWardrobe,
            icon = { Icon(Icons.Outlined.Checkroom, contentDescription = null) },
            label = { Text("Wardrobe", style = MaterialTheme.typography.labelSmall) },
            colors = colors
        )
        NavigationBarItem(
            selected = selectedDestination == MyWardrobeDestination.ADD,
            onClick = onAddItem,
            icon = { Icon(Icons.Outlined.AddAPhoto, contentDescription = null) },
            label = { Text("Add", style = MaterialTheme.typography.labelSmall) },
            colors = colors
        )
        NavigationBarItem(
            selected = selectedDestination == MyWardrobeDestination.OUTFITS,
            onClick = onViewOutfits,
            icon = { Icon(Icons.Outlined.Style, contentDescription = null) },
            label = { Text("Fits", style = MaterialTheme.typography.labelSmall) },
            colors = colors
        )
    }
}
