package com.paddysystems.mywardrobe.ui.screens.createoutfit

import com.paddysystems.mywardrobe.data.model.WardrobeItem

fun cycleOutfitItem(
    currentItemId: String?,
    items: List<WardrobeItem>,
    direction: Int
): String? {

    if (items.isEmpty()) {
        return null
    }

    val currentIndex =
        items.indexOfFirst {
            it.id == currentItemId
        }

    if (currentIndex == -1) {
        return if (direction >= 0) {
            items.first().id
        } else {
            items.last().id
        }
    }

    val nextIndex =
        (
                currentIndex +
                        direction
                ).floorMod(
                items.size
            )

    return items[nextIndex].id
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