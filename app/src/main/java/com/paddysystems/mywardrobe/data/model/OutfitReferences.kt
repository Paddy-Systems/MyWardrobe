package com.paddysystems.mywardrobe.data.model

fun Outfit.referencedItemIds(): Set<String> {
    return buildSet {
        layers.forEach { layer ->
            layer.top.itemId?.let(::add)
            layer.bottom.itemId?.let(::add)
            layer.fullLength.itemId?.let(::add)
        }

        shoes.itemId?.let(::add)
        bag.itemId?.let(::add)

        accessories.forEach { slot ->
            slot.itemId?.let(::add)
        }
    }
}

fun Outfit.selectedItemIds(): Set<String> {
    return buildSet {
        layers.forEach { layer ->
            when (layer.mode) {
                OutfitLayerMode.SEPARATES -> {
                    layer.top.itemId?.let(::add)
                    layer.bottom.itemId?.let(::add)
                }

                OutfitLayerMode.FULL_LENGTH -> {
                    layer.fullLength.itemId?.let(::add)
                }
            }
        }

        shoes.itemId?.let(::add)
        bag.itemId?.let(::add)

        accessories.forEach { slot ->
            slot.itemId?.let(::add)
        }
    }
}

fun Outfit.containsItem(itemId: String): Boolean {
    return itemId in selectedItemIds()
}

fun Outfit.referencesItem(itemId: String): Boolean {
    return itemId in referencedItemIds()
}

fun Outfit.withoutWardrobeItem(itemId: String): Outfit {
    fun clearIfMatching(slot: OutfitSlotSelection): OutfitSlotSelection {
        return if (slot.itemId == itemId) {
            OutfitSlotSelection()
        } else {
            slot
        }
    }

    return copy(
        layers = layers.map { layer ->
            layer.copy(
                top = clearIfMatching(layer.top),
                bottom = clearIfMatching(layer.bottom),
                fullLength = clearIfMatching(layer.fullLength)
            )
        },
        shoes = clearIfMatching(shoes),
        bag = clearIfMatching(bag),
        accessories = accessories.filterNot { it.itemId == itemId }
    )
}
