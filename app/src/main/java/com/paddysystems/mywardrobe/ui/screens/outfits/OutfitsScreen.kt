package com.paddysystems.mywardrobe.ui.screens.outfits

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.paddysystems.mywardrobe.data.storage.loadOutfits
import com.paddysystems.mywardrobe.data.storage.loadWardrobeItems
import com.paddysystems.mywardrobe.ui.components.EditorialPageHeader
import com.paddysystems.mywardrobe.ui.components.EditorialPrimaryButton
import com.paddysystems.mywardrobe.ui.screens.outfits.components.OutfitThumbnail

@Composable
fun OutfitsScreen(
    refreshKey: Int = 0,
    onCreateOutfit: () -> Unit = {},
    onOutfitClick: (String) -> Unit = {}
) {
    val context = LocalContext.current

    val outfits = remember(refreshKey) {
        loadOutfits(context)
    }
    val wardrobeItems = remember {
        loadWardrobeItems(context)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 22.dp)
    ) {
        EditorialPageHeader(
            eyebrow = "Your lookbook",
            title = "Saved outfits",
            subtitle = if (outfits.isEmpty()) {
                "A home for combinations worth repeating."
            } else {
                "${outfits.size} looks, ready when you are."
            },
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(Modifier.height(16.dp))

        EditorialPrimaryButton(
            text = "Build a new look",
            icon = Icons.Outlined.Add,
            onClick = onCreateOutfit,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        )

        if (outfits.isEmpty()) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(Modifier.padding(24.dp)) {
                    Text(
                        "Your first look starts here",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        "Choose a top, bottom and finishing pieces from your wardrobe.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(14.dp)
            ) {
                items(
                    items = outfits,
                    key = { it.id }
                ) { outfit ->
                    Surface(
                        onClick = { onOutfitClick(outfit.id) },
                        modifier = Modifier.padding(6.dp),
                        shape = RoundedCornerShape(20.dp),
                        tonalElevation = 1.dp
                    ) {
                        Column {
                            OutfitThumbnail(
                                outfit = outfit,
                                wardrobeItems = wardrobeItems,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(0.78f)
                            )

                            Text(
                                text = outfit.name,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
