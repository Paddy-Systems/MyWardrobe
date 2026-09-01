package com.paddysystems.mywardrobe.ui.screens.createoutfit

import com.paddysystems.mywardrobe.data.model.Outfit
import com.paddysystems.mywardrobe.data.model.OutfitSlotSelection
import com.paddysystems.mywardrobe.data.model.WardrobeItem

fun sanitiseOutfitForEditing(
    outfit: Outfit,
    wardrobeItems: List<WardrobeItem>
): Outfit {
    val validIds = wardrobeItems
        .map { it.id }
        .toSet()

    fun sanitiseSlot(
        slot: OutfitSlotSelection
    ): OutfitSlotSelection {
        return if (
            slot.itemId == null ||
            slot.itemId in validIds
        ) {
            slot
        } else {
            slot.copy(
                itemId = null,
                isLocked = false
            )
        }
    }

    return outfit.copy(
        layers = outfit.layers.map { layer ->
            layer.copy(
                top = sanitiseSlot(layer.top),
                bottom = sanitiseSlot(layer.bottom),
                fullLength = sanitiseSlot(layer.fullLength)
            )
        },
        shoes = sanitiseSlot(outfit.shoes),
        bag = sanitiseSlot(outfit.bag),
        accessories = outfit.accessories.map(::sanitiseSlot)
    )
}
