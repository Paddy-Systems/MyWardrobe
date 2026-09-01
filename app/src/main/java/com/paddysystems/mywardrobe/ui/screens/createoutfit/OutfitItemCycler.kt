package com.paddysystems.mywardrobe.ui.screens.createoutfit

import com.paddysystems.mywardrobe.data.model.OutfitLayer
import com.paddysystems.mywardrobe.data.model.OutfitLayerMode
import com.paddysystems.mywardrobe.data.model.WardrobeItem

fun cycleOutfitItem(
    currentItemId: String?,
    items: List<WardrobeItem>,
    direction: Int
): String? {

    /*
     * Empty is a real position in the cycle:
     *
     * Empty
     * Item 1
     * Item 2
     * Item 3
     * Empty
     */

    val itemIds =
        listOf<String?>(null) +
                items.map {
                    it.id
                }

    if (itemIds.size == 1) {
        return null
    }

    val currentIndex =
        itemIds.indexOf(
            currentItemId
        )
            .takeIf {
                it >= 0
            }
            ?: 0

    val nextIndex =
        (
                currentIndex +
                        direction
                ).floorMod(
                itemIds.size
            )

    return itemIds[
        nextIndex
    ]
}

private fun Int.floorMod(
    divisor: Int
): Int {
    return (
            (
                    this % divisor
                    ) + divisor
            ) % divisor
}

fun shuffleOutfitLayer(
    layer: OutfitLayer,
    topItems: List<WardrobeItem>,
    bottomItems: List<WardrobeItem>,
    fullLengthItems: List<WardrobeItem>
): OutfitLayer {

    return when (layer.mode) {

        OutfitLayerMode.SEPARATES ->
            layer.copy(
                top =
                    if (
                        layer.top.isLocked
                    ) {
                        layer.top
                    } else {
                        layer.top.copy(
                            itemId =
                                randomDifferentItemId(
                                    currentItemId =
                                        layer.top.itemId,
                                    items =
                                        topItems
                                )
                        )
                    },

                bottom =
                    if (
                        layer.bottom.isLocked
                    ) {
                        layer.bottom
                    } else {
                        layer.bottom.copy(
                            itemId =
                                randomDifferentItemId(
                                    currentItemId =
                                        layer.bottom.itemId,
                                    items =
                                        bottomItems
                                )
                        )
                    }
            )

        OutfitLayerMode.FULL_LENGTH ->
            layer.copy(
                fullLength =
                    if (
                        layer
                            .fullLength
                            .isLocked
                    ) {
                        layer.fullLength
                    } else {
                        layer.fullLength.copy(
                            itemId =
                                randomDifferentItemId(
                                    currentItemId =
                                        layer
                                            .fullLength
                                            .itemId,
                                    items =
                                        fullLengthItems
                                )
                        )
                    }
            )
    }
}

private fun randomDifferentItemId(
    currentItemId: String?,
    items: List<WardrobeItem>
): String? {

    if (items.isEmpty()) {
        return null
    }

    if (items.size == 1) {
        return items.first().id
    }

    return items
        .filterNot {
            it.id == currentItemId
        }
        .random()
        .id
}