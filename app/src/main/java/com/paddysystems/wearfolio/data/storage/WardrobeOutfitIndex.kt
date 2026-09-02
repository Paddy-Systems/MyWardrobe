package com.paddysystems.wearfolio.data.storage

import android.content.Context
import com.paddysystems.wearfolio.data.model.Outfit
import com.paddysystems.wearfolio.data.model.containsItem
import com.paddysystems.wearfolio.data.model.referencesItem
import com.paddysystems.wearfolio.data.model.withoutWardrobeItem

fun loadOutfitsUsingItem(
    context: Context,
    profileId: String,
    itemId: String
): List<Outfit> {
    return loadOutfits(context, profileId)
        .filter { it.containsItem(itemId) }
}

fun syncWardrobeOutfitIds(
    context: Context,
    profileId: String
): Boolean {
    val outfits = loadOutfits(context, profileId)
    val wardrobeItems = loadWardrobeItems(context, profileId)
    var success = true

    wardrobeItems.forEach { item ->
        val outfitIds = outfits
            .filter { it.containsItem(item.id) }
            .map { it.id }

        if (outfitIds != item.outfitIds) {
            val updated = updateWardrobeItemOutfitIds(
                context = context,
                profileId = profileId,
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
    profileId: String,
    itemId: String
): Boolean {
    val affectedOutfits = loadOutfits(context, profileId)
        .filter { it.referencesItem(itemId) }

    var success = true

    affectedOutfits.forEach { outfit ->
        val updated = outfit.withoutWardrobeItem(itemId)

        if (!saveOutfit(context, profileId, updated)) {
            success = false
        }
    }

    return success
}
