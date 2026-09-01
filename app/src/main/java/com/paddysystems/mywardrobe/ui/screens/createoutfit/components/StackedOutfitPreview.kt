package com.paddysystems.mywardrobe.ui.screens.createoutfit.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paddysystems.mywardrobe.data.model.OutfitLayer
import com.paddysystems.mywardrobe.data.model.OutfitLayerMode
import com.paddysystems.mywardrobe.data.model.WardrobeItem

@Composable
fun StackedOutfitPreview(
    layers: List<OutfitLayer>,
    wardrobeItems: List<WardrobeItem>,
    modifier: Modifier = Modifier
) {
    val itemsById =
        remember(wardrobeItems) {
            wardrobeItems.associateBy {
                it.id
            }
        }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(500.dp),

        shape =
            RoundedCornerShape(
                24.dp
            ),

        color =
            MaterialTheme
                .colorScheme
                .surfaceVariant
                .copy(
                    alpha = 0.45f
                )
    ) {
        Box(
            modifier =
                Modifier.fillMaxSize()
        ) {
            OutfitSilhouette(
                modifier = Modifier
                    .matchParentSize()
                    .padding(
                        horizontal = 52.dp,
                        vertical = 20.dp
                    )
            )

            /*
             * Important:
             *
             * Layers are drawn in order.
             *
             * Layer 1 first.
             * Layer 2 over it.
             * Layer 3 over that.
             */
            layers.forEach { layer ->

                when (layer.mode) {

                    OutfitLayerMode.SEPARATES -> {

                        layer.top.itemId
                            ?.let {
                                itemsById[it]
                            }
                            ?.let { item ->

                                OutfitGarmentImage(
                                    item = item,

                                    modifier =
                                        Modifier
                                            .align(
                                                Alignment
                                                    .TopCenter
                                            )
                                            .padding(
                                                top =
                                                    38.dp
                                            )
                                            .fillMaxWidth(
                                                0.80f
                                            )
                                            .height(
                                                215.dp
                                            )
                                )
                            }

                        layer.bottom.itemId
                            ?.let {
                                itemsById[it]
                            }
                            ?.let { item ->

                                OutfitGarmentImage(
                                    item = item,

                                    modifier =
                                        Modifier
                                            .align(
                                                Alignment
                                                    .BottomCenter
                                            )
                                            .padding(
                                                bottom =
                                                    18.dp
                                            )
                                            .fillMaxWidth(
                                                0.72f
                                            )
                                            .height(
                                                265.dp
                                            )
                                )
                            }
                    }

                    OutfitLayerMode.FULL_LENGTH -> {

                        layer.fullLength.itemId
                            ?.let {
                                itemsById[it]
                            }
                            ?.let { item ->

                                OutfitGarmentImage(
                                    item = item,

                                    modifier =
                                        Modifier
                                            .align(
                                                Alignment.Center
                                            )
                                            .fillMaxWidth(
                                                0.82f
                                            )
                                            .fillMaxHeight(
                                                0.90f
                                            )
                                            .padding(
                                                vertical =
                                                    24.dp
                                            )
                                )
                            }
                    }
                }
            }
        }
    }
}