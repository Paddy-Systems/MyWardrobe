package com.paddysystems.mywardrobe.data.storage

import android.content.Context
import com.paddysystems.mywardrobe.data.model.WardrobeItem

fun updateWardrobeItemCutout(context: Context, item: WardrobeItem, cutoutPath: String): WardrobeItem? {
    val updated = item.copy(cutoutPath = cutoutPath)
    return runCatching {
        WardrobeStoragePaths.item(context, item.id).writeText(WardrobeItemJsonCodec.encode(updated).toString())
        updated
    }.getOrNull()
}
