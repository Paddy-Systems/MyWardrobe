package com.paddysystems.mywardrobe.data.storage

import android.content.Context
import com.paddysystems.mywardrobe.data.model.WardrobeItem

fun updateWardrobeItem(
    context: Context,
    item: WardrobeItem,
    clothingTypeId: String,
    colours: List<String>
): Boolean = runCatching {
    val updated = item.copy(clothingTypeId = clothingTypeId, colours = colours)
    WardrobeStoragePaths.item(context, item.id).writeText(WardrobeItemJsonCodec.encode(updated).toString())
}.isSuccess
