package com.paddysystems.mywardrobe.ui.screens.outfits.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paddysystems.mywardrobe.data.model.OutfitLayer
import com.paddysystems.mywardrobe.data.model.OutfitLayerMode
import com.paddysystems.mywardrobe.data.model.WardrobeItem
import com.paddysystems.mywardrobe.ui.screens.createoutfit.components.OutfitGarmentImage

@Composable
fun SavedOutfitLayerPreview(
    layer: OutfitLayer,
    layerNumber: Int,
    wardrobeItems: List<WardrobeItem>,
    modifier: Modifier = Modifier
) {
    val itemsById = remember(wardrobeItems) {
        wardrobeItems.associateBy { it.id }
    }

    val topItem = layer.top.itemId?.let(itemsById::get)
    val bottomItem = layer.bottom.itemId?.let(itemsById::get)
    val fullLengthItem = layer.fullLength.itemId?.let(itemsById::get)

    val hasClothing = when (layer.mode) {
        OutfitLayerMode.SEPARATES -> topItem != null || bottomItem != null
        OutfitLayerMode.FULL_LENGTH -> fullLengthItem != null
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(460.dp),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            Text(
                text = "LAYER $layerNumber",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.TopStart)
            )

            if (!hasClothing) {
                Text(
                    text = "Empty layer",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.Center)
                )
                return@Box
            }

            when (layer.mode) {
                OutfitLayerMode.SEPARATES -> {
                    topItem?.let { item ->
                        OutfitGarmentImage(
                            item = item,
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(top = 30.dp)
                                .fillMaxWidth(0.78f)
                                .height(200.dp)
                        )
                    }

                    bottomItem?.let { item ->
                        OutfitGarmentImage(
                            item = item,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 12.dp)
                                .fillMaxWidth(0.72f)
                                .height(250.dp)
                        )
                    }
                }

                OutfitLayerMode.FULL_LENGTH -> {
                    fullLengthItem?.let { item ->
                        OutfitGarmentImage(
                            item = item,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(
                                    top = 28.dp,
                                    start = 24.dp,
                                    end = 24.dp,
                                    bottom = 12.dp
                                )
                        )
                    }
                }
            }
        }
    }
}
