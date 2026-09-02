package com.paddysystems.mywardrobe.data.storage

import android.content.Context
import com.paddysystems.mywardrobe.data.model.Outfit
import com.paddysystems.mywardrobe.data.model.containsItem
import com.paddysystems.mywardrobe.data.model.referencesItem
import com.paddysystems.mywardrobe.data.model.withoutWardrobeItem

fun loadOutfitsUsingItem(
    context: Context,
    itemId: String
): List<Outfit> {
    return loadOutfits(context)
        .filter { it.containsItem(itemId) }
}

fun syncWardrobeOutfitIds(context: Context): Boolean {
    val outfits = loadOutfits(context)
    val wardrobeItems = loadWardrobeItems(context)
    var success = true

    wardrobeItems.forEach { item ->
        val outfitIds = outfits
            .filter { it.containsItem(item.id) }
            .map { it.id }

        if (outfitIds != item.outfitIds) {
            val updated = updateWardrobeItemOutfitIds(
                context = context,
                item = item,
                outfitIds = outfitIds
            )

            if (!updated) {
                success = false
            }
        }
    }

    return success
}

fun removeWardrobeItemFromOutfits(
    context: Context,
    itemId: String
): Boolean {
    val affectedOutfits = loadOutfits(context)
        .filter { it.referencesItem(itemId) }

    var success = true

    affectedOutfits.forEach { outfit ->
        val updated = outfit.withoutWardrobeItem(itemId)

        if (!saveOutfit(context, updated)) {
            success = false
        }
    }

    return success
}
