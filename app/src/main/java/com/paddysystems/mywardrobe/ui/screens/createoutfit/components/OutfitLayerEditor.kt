package com.paddysystems.mywardrobe.ui.screens.createoutfit.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paddysystems.mywardrobe.data.model.OutfitLayer
import com.paddysystems.mywardrobe.data.model.OutfitLayerMode
import com.paddysystems.mywardrobe.data.model.OutfitPlacement
import com.paddysystems.mywardrobe.data.model.WardrobeItem
import com.paddysystems.mywardrobe.ui.screens.createoutfit.cycleOutfitItem

@Composable
fun OutfitLayerEditor(
    layer: OutfitLayer,
    topItems: List<WardrobeItem>,
    bottomItems: List<WardrobeItem>,
    fullLengthItems:
    List<WardrobeItem>,
    onLayerChange:
        (OutfitLayer) -> Unit,
    onSearch:
        (OutfitPlacement) -> Unit,
    modifier: Modifier = Modifier
) {
    val topItem =
        topItems.firstOrNull {
            it.id ==
                    layer.top.itemId
        }

    val bottomItem =
        bottomItems.firstOrNull {
            it.id ==
                    layer.bottom.itemId
        }

    val fullLengthItem =
        fullLengthItems.firstOrNull {
            it.id ==
                    layer.fullLength.itemId
        }

    Column(
        modifier = modifier
    ) {
        Text(
            text = "Layer 1",

            style =
                MaterialTheme
                    .typography
                    .titleLarge
        )

        Spacer(
            modifier =
                Modifier.height(8.dp)
        )

        Row(
            modifier =
                Modifier.fillMaxWidth()
        ) {
            if (
                layer.mode ==
                OutfitLayerMode.SEPARATES
            ) {
                Button(
                    modifier =
                        Modifier.weight(1f),

                    onClick = {}
                ) {
                    Text("Top + Bottom")
                }
            } else {
                OutlinedButton(
                    modifier =
                        Modifier.weight(1f),

                    onClick = {
                        onLayerChange(
                            layer.copy(
                                mode =
                                    OutfitLayerMode
                                        .SEPARATES
                            )
                        )
                    }
                ) {
                    Text("Top + Bottom")
                }
            }

            Spacer(
                modifier =
                    Modifier.padding(
                        horizontal = 4.dp
                    )
            )

            if (
                layer.mode ==
                OutfitLayerMode.FULL_LENGTH
            ) {
                Button(
                    modifier =
                        Modifier.weight(1f),

                    onClick = {}
                ) {
                    Text("Full Length")
                }
            } else {
                OutlinedButton(
                    modifier =
                        Modifier.weight(1f),

                    onClick = {
                        onLayerChange(
                            layer.copy(
                                mode =
                                    OutfitLayerMode
                                        .FULL_LENGTH
                            )
                        )
                    }
                ) {
                    Text("Full Length")
                }
            }
        }

        Spacer(
            modifier =
                Modifier.height(12.dp)
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(480.dp),

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
            Box {
                OutfitSilhouette(
                    modifier = Modifier
                        .matchParentSize()
                        .padding(
                            horizontal =
                                52.dp,

                            vertical =
                                20.dp
                        )
                )

                when (layer.mode) {

                    OutfitLayerMode
                        .SEPARATES -> {

                        topItem?.let { item ->
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
                                                50.dp
                                        )
                                        .fillMaxWidth(
                                            0.80f
                                        )
                                        .height(
                                            225.dp
                                        )
                            )
                        }

                        bottomItem
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
                                                    30.dp
                                            )
                                            .fillMaxWidth(
                                                0.72f
                                            )
                                            .height(
                                                275.dp
                                            )
                                )
                            }
                    }

                    OutfitLayerMode
                        .FULL_LENGTH -> {

                        fullLengthItem
                            ?.let { item ->

                                OutfitGarmentImage(
                                    item = item,

                                    modifier =
                                        Modifier
                                            .align(
                                                Alignment
                                                    .Center
                                            )
                                            .fillMaxWidth(
                                                0.82f
                                            )
                                            .fillMaxHeight(
                                                0.90f
                                            )
                                            .padding(
                                                vertical =
                                                    16.dp
                                            )
                                )
                            }
                    }
                }
            }
        }

        Spacer(
            modifier =
                Modifier.height(12.dp)
        )

        when (layer.mode) {

            OutfitLayerMode
                .SEPARATES -> {

                OutfitSlotControls(
                    label = "Top",
                    item = topItem,
                    isLocked =
                        layer.top.isLocked,
                    hasItems =
                        topItems.isNotEmpty(),

                    onPrevious = {
                        onLayerChange(
                            layer.copy(
                                top =
                                    layer.top.copy(
                                        itemId =
                                            cycleOutfitItem(
                                                currentItemId =
                                                    layer
                                                        .top
                                                        .itemId,

                                                items =
                                                    topItems,

                                                direction =
                                                    -1
                                            )
                                    )
                            )
                        )
                    },

                    onNext = {
                        onLayerChange(
                            layer.copy(
                                top =
                                    layer.top.copy(
                                        itemId =
                                            cycleOutfitItem(
                                                currentItemId =
                                                    layer
                                                        .top
                                                        .itemId,

                                                items =
                                                    topItems,

                                                direction =
                                                    1
                                            )
                                    )
                            )
                        )
                    },

                    onToggleLock = {
                        onLayerChange(
                            layer.copy(
                                top =
                                    layer.top.copy(
                                        isLocked =
                                            !layer
                                                .top
                                                .isLocked
                                    )
                            )
                        )
                    },

                    onSearch = {
                        onSearch(
                            OutfitPlacement.TOP
                        )
                    }
                )

                OutfitSlotControls(
                    label = "Bottom",
                    item = bottomItem,
                    isLocked =
                        layer.bottom
                            .isLocked,
                    hasItems =
                        bottomItems
                            .isNotEmpty(),

                    onPrevious = {
                        onLayerChange(
                            layer.copy(
                                bottom =
                                    layer.bottom.copy(
                                        itemId =
                                            cycleOutfitItem(
                                                layer.bottom
                                                    .itemId,

                                                bottomItems,

                                                -1
                                            )
                                    )
                            )
                        )
                    },

                    onNext = {
                        onLayerChange(
                            layer.copy(
                                bottom =
                                    layer.bottom.copy(
                                        itemId =
                                            cycleOutfitItem(
                                                layer.bottom
                                                    .itemId,

                                                bottomItems,

                                                1
                                            )
                                    )
                            )
                        )
                    },

                    onToggleLock = {
                        onLayerChange(
                            layer.copy(
                                bottom =
                                    layer.bottom.copy(
                                        isLocked =
                                            !layer
                                                .bottom
                                                .isLocked
                                    )
                            )
                        )
                    },

                    onSearch = {
                        onSearch(
                            OutfitPlacement.BOTTOM
                        )
                    }
                )
            }

            OutfitLayerMode
                .FULL_LENGTH -> {

                OutfitSlotControls(
                    label =
                        "Full Length",

                    item =
                        fullLengthItem,

                    isLocked =
                        layer.fullLength
                            .isLocked,

                    hasItems =
                        fullLengthItems
                            .isNotEmpty(),

                    onPrevious = {
                        onLayerChange(
                            layer.copy(
                                fullLength =
                                    layer.fullLength
                                        .copy(
                                            itemId =
                                                cycleOutfitItem(
                                                    layer
                                                        .fullLength
                                                        .itemId,

                                                    fullLengthItems,

                                                    -1
                                                )
                                        )
                            )
                        )
                    },

                    onNext = {
                        onLayerChange(
                            layer.copy(
                                fullLength =
                                    layer.fullLength
                                        .copy(
                                            itemId =
                                                cycleOutfitItem(
                                                    layer
                                                        .fullLength
                                                        .itemId,

                                                    fullLengthItems,

                                                    1
                                                )
                                        )
                            )
                        )
                    },

                    onToggleLock = {
                        onLayerChange(
                            layer.copy(
                                fullLength =
                                    layer.fullLength
                                        .copy(
                                            isLocked =
                                                !layer
                                                    .fullLength
                                                    .isLocked
                                        )
                            )
                        )
                    },

                    onSearch = {
                        onSearch(
                            OutfitPlacement
                                .FULL_LENGTH
                        )
                    }
                )
            }
        }
    }
}