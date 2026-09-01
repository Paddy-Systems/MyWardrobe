package com.paddysystems.mywardrobe.ui.screens.outfits.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paddysystems.mywardrobe.data.model.Outfit
import com.paddysystems.mywardrobe.data.model.WardrobeItem
import com.paddysystems.mywardrobe.data.model.defaultClothingTypes
import com.paddysystems.mywardrobe.data.model.wardrobeLabel
import com.paddysystems.mywardrobe.ui.screens.createoutfit.components.OutfitGarmentImage

private data class FinishingPiece(
    val label: String,
    val item: WardrobeItem
)

@Composable
fun OutfitFinishingPieces(
    outfit: Outfit,
    wardrobeItems: List<WardrobeItem>,
    modifier: Modifier = Modifier
) {
    val itemsById = remember(wardrobeItems) {
        wardrobeItems.associateBy { it.id }
    }

    val pieces = remember(outfit, wardrobeItems) {
        buildList {
            outfit.shoes.itemId
                ?.let(itemsById::get)
                ?.let { add(FinishingPiece("Shoes", it)) }

            outfit.bag.itemId
                ?.let(itemsById::get)
                ?.let { add(FinishingPiece("Bag", it)) }

            outfit.accessories.forEach { slot ->
                slot.itemId
                    ?.let(itemsById::get)
                    ?.let { item ->
                        val label = defaultClothingTypes
                            .firstOrNull { it.id == item.clothingTypeId }
                            ?.name
                            ?: wardrobeLabel(item.clothingTypeId)

                        add(FinishingPiece(label, item))
                    }
            }
        }
    }

    if (pieces.isEmpty()) {
        return
    }

    Column(modifier = modifier) {
        Text(
            text = "FINISHING PIECES",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.height(10.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(pieces) { piece ->
                Column(modifier = Modifier.width(118.dp)) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp),
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        OutfitGarmentImage(
                            item = piece.item,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp)
                        )
                    }

                    Text(
                        text = piece.label,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            }
        }
    }
}
