package com.paddysystems.mywardrobe.ui.screens.createoutfit.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.clickable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.paddysystems.mywardrobe.data.model.OutfitLayer
import com.paddysystems.mywardrobe.data.model.OutfitLayerMode
import com.paddysystems.mywardrobe.data.model.WardrobeItem
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding

private const val CARD_PEEK_FRACTION =
    0.25f

@Composable
fun StackedOutfitPreview(
    layers: List<OutfitLayer>,
    wardrobeItems: List<WardrobeItem>,
    onLayerClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val itemsById =
        remember(wardrobeItems) {
            wardrobeItems.associateBy {
                it.id
            }
        }

    if (layers.isEmpty()) {
        return
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(500.dp)
    ) {
        /*
         * Example with 3 cards:
         *
         * full card width
         * + 25% peek
         * + 25% peek
         *
         * must equal available width.
         */
        val multiplier =
            1f +
                    (
                            CARD_PEEK_FRACTION *
                                    (layers.size - 1)
                            )

        val cardWidth =
            maxWidth /
                    multiplier

        val peekWidth =
            cardWidth *
                    CARD_PEEK_FRACTION

        Box(
            modifier =
                Modifier.fillMaxSize()
        ) {
            layers.forEachIndexed {
                    index,
                    layer ->

                LayerStackCard(
                    layer = layer,

                    layerNumber =
                        index + 1,

                    itemsById =
                        itemsById,

                    width =
                        cardWidth,

                    offsetX =
                        peekWidth *
                                index,

                    onClick = {
                        onLayerClick(
                            index
                        )
                    },

                    modifier =
                        Modifier.zIndex(
                            index.toFloat()
                        )
                )
            }
        }
    }
}

@Composable
private fun LayerStackCard(
    layer: OutfitLayer,
    layerNumber: Int,
    itemsById:
    Map<String, WardrobeItem>,
    width: Dp,
    offsetX: Dp,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .offset(
                x = offsetX
            )
            .width(width)
            .height(480.dp)
            .clickable(
                onClick = onClick
            )
    ) {
        LayerPreviewContents(
            layer =
                layer,

            layerNumber =
                layerNumber,

            itemsById =
                itemsById
        )
    }
}

@Composable
private fun LayerPreviewContents(
    layer: OutfitLayer,
    layerNumber: Int,
    itemsById:
    Map<String, WardrobeItem>
) {
    val topItem =
        layer.top.itemId
            ?.let {
                itemsById[it]
            }

    val bottomItem =
        layer.bottom.itemId
            ?.let {
                itemsById[it]
            }

    val fullLengthItem =
        layer.fullLength.itemId
            ?.let {
                itemsById[it]
            }

    val hasClothing =
        when (layer.mode) {

            OutfitLayerMode.SEPARATES ->
                topItem != null ||
                        bottomItem != null

            OutfitLayerMode.FULL_LENGTH ->
                fullLengthItem != null
        }

    Box(
        modifier =
            Modifier.fillMaxSize()
    ) {
        Text(
            text =
                "Layer $layerNumber",

            modifier =
                Modifier
                    .align(
                        Alignment.TopStart
                    )
                    .padding(
                        14.dp
                    ),

            style =
                MaterialTheme
                    .typography
                    .titleMedium
        )

        if (!hasClothing) {
            Text(
                text = "Empty",

                modifier = Modifier
                    .align(
                        Alignment.Center
                    ),

                color =
                    MaterialTheme
                        .colorScheme
                        .onSurfaceVariant
            )

            return@Box
        }

        when (layer.mode) {

            OutfitLayerMode.SEPARATES -> {

                topItem?.let { item ->

                    OutfitGarmentImage(
                        item = item,

                        modifier = Modifier
                            .align(
                                Alignment.TopCenter
                            )
                            .padding(
                                top = 50.dp
                            )
                            .fillMaxWidth(
                                0.82f
                            )
                            .height(
                                185.dp
                            )
                    )
                }

                bottomItem
                    ?.let { item ->

                        OutfitGarmentImage(
                            item = item,

                            modifier = Modifier
                                .align(
                                    Alignment
                                        .BottomCenter
                                )
                                .padding(
                                    bottom =
                                        20.dp
                                )
                                .fillMaxWidth(
                                    0.76f
                                )
                                .height(
                                    235.dp
                                )
                        )
                    }
            }

            OutfitLayerMode.FULL_LENGTH -> {

                fullLengthItem
                    ?.let { item ->

                        OutfitGarmentImage(
                            item = item,

                            modifier = Modifier
                                .fillMaxSize()
                                .padding(
                                    top = 46.dp,
                                    start = 20.dp,
                                    end = 20.dp,
                                    bottom = 16.dp
                                )
                        )
                    }
            }
        }
    }
}