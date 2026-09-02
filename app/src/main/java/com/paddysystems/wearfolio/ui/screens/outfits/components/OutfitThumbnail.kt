package com.paddysystems.wearfolio.ui.screens.outfits.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paddysystems.wearfolio.data.model.Outfit
import com.paddysystems.wearfolio.data.model.OutfitLayerMode
import com.paddysystems.wearfolio.data.model.WardrobeItem
import com.paddysystems.wearfolio.ui.screens.createoutfit.components.OutfitGarmentImage

@Composable
fun OutfitThumbnail(
    outfit: Outfit,
    wardrobeItems:
    List<WardrobeItem>,
    modifier: Modifier = Modifier
) {
    val itemsById =
        remember(wardrobeItems) {
            wardrobeItems.associateBy {
                it.id
            }
        }

    val primaryLayer =
        outfit.layers
            .asReversed()
            .firstOrNull { layer ->

                when (layer.mode) {

                    OutfitLayerMode.SEPARATES ->
                        layer.top.itemId != null ||
                                layer.bottom.itemId != null

                    OutfitLayerMode.FULL_LENGTH ->
                        layer.fullLength
                            .itemId != null
                }
            }

    Surface(
        modifier = modifier,

        shape =
            RoundedCornerShape(
                18.dp
            ),

        color =
            MaterialTheme
                .colorScheme
                .surface
    ) {
        Box(
            modifier =
                Modifier.fillMaxSize()
        ) {

            when (
                primaryLayer?.mode
            ) {

                OutfitLayerMode.SEPARATES -> {

                    primaryLayer.top.itemId
                        ?.let {
                            itemsById[it]
                        }
                        ?.let { item ->

                            OutfitGarmentImage(
                                item = item,

                                modifier = Modifier
                                    .align(
                                        Alignment.TopCenter
                                    )
                                    .padding(
                                        top = 12.dp
                                    )
                                    .fillMaxWidth(
                                        0.72f
                                    )
                                    .height(
                                        115.dp
                                    )
                            )
                        }

                    primaryLayer
                        .bottom
                        .itemId
                        ?.let {
                            itemsById[it]
                        }
                        ?.let { item ->

                            OutfitGarmentImage(
                                item = item,

                                modifier = Modifier
                                    .align(
                                        Alignment.Center
                                    )
                                    .padding(
                                        top = 80.dp
                                    )
                                    .fillMaxWidth(
                                        0.68f
                                    )
                                    .height(
                                        145.dp
                                    )
                            )
                        }
                }

                OutfitLayerMode.FULL_LENGTH -> {

                    primaryLayer
                        .fullLength
                        .itemId
                        ?.let {
                            itemsById[it]
                        }
                        ?.let { item ->

                            OutfitGarmentImage(
                                item = item,

                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(
                                        18.dp
                                    )
                            )
                        }
                }

                null -> Unit
            }

            outfit.shoes.itemId
                ?.let {
                    itemsById[it]
                }
                ?.let { item ->

                    OutfitGarmentImage(
                        item = item,

                        modifier = Modifier
                            .align(
                                Alignment.BottomCenter
                            )
                            .fillMaxWidth(
                                0.44f
                            )
                            .height(
                                52.dp
                            )
                            .padding(
                                bottom = 4.dp
                            )
                    )
                }

            outfit.bag.itemId
                ?.let {
                    itemsById[it]
                }
                ?.let { item ->

                    OutfitGarmentImage(
                        item = item,

                        modifier = Modifier
                            .align(
                                Alignment.TopEnd
                            )
                            .padding(
                                8.dp
                            )
                            .height(
                                58.dp
                            )
                            .fillMaxWidth(
                                0.28f
                            )
                    )
                }

            if (
                outfit.layers.size > 1
            ) {
                Text(
                    text =
                        "${outfit.layers.size} layers",

                    modifier = Modifier
                        .align(
                            Alignment.BottomStart
                        )
                        .padding(
                            8.dp
                        ),

                    style =
                        MaterialTheme
                            .typography
                            .labelSmall
                )
            }
        }
    }
}
