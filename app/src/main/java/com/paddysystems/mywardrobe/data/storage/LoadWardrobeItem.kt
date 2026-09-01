package com.paddysystems.mywardrobe.data.storage

import android.content.Context
import com.paddysystems.mywardrobe.data.model.WardrobeItem
import org.json.JSONObject
import java.io.File

fun loadWardrobeItem(context: Context, itemId: String): WardrobeItem? {
    val file = WardrobeStoragePaths.item(context, itemId).takeIf(File::exists) ?: return null
    return runCatching { WardrobeItemJsonCodec.decode(JSONObject(file.readText()), file.lastModified()) }
        .getOrNull()
        ?.takeIf { File(it.imagePath).exists() }
}
