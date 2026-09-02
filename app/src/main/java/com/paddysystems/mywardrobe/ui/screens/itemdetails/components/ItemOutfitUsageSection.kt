package com.paddysystems.mywardrobe.ui.screens.itemdetails.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paddysystems.mywardrobe.data.model.Outfit
import com.paddysystems.mywardrobe.data.model.WardrobeItem
import com.paddysystems.mywardrobe.ui.screens.outfits.components.OutfitThumbnail

@Composable
fun ItemOutfitUsageSection(
    outfits: List<Outfit>,
    wardrobeItems: List<WardrobeItem>,
    onOutfitClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "SAVED FITS",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.height(10.dp))

        if (outfits.isEmpty()) {
            Text(
                text = "Not used in a saved fit yet.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            return@Column
        }

        LazyRow(
            state = rememberLazyListState()
        ) {
            items(
                items = outfits,
                key = { it.id }
            ) { outfit ->
                Surface(
                    onClick = {
                        onOutfitClick(outfit.id)
                    },
                    modifier = Modifier
                        .width(150.dp)
                        .padding(end = 10.dp),
                    shape = MaterialTheme.shapes.large,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column {
                        OutfitThumbnail(
                            outfit = outfit,
                            wardrobeItems = wardrobeItems,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                        )

                        Text(
                            text = outfit.name,
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
        }
    }
}
