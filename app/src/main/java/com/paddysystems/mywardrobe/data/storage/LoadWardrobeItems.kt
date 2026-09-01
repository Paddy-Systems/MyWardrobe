package com.paddysystems.mywardrobe.data.storage

import android.content.Context
import com.paddysystems.mywardrobe.data.model.WardrobeItem
import org.json.JSONObject
import java.io.File

fun loadWardrobeItems(context: Context): List<WardrobeItem> =
    WardrobeStoragePaths.itemDirectory(context)
        .listFiles()
        ?.asSequence()
        ?.filter { it.isFile && it.extension == "json" }
        ?.mapNotNull { file ->
            runCatching { WardrobeItemJsonCodec.decode(JSONObject(file.readText()), file.lastModified()) }
                .getOrNull()
                ?.takeIf { File(it.imagePath).exists() }
        }
        ?.sortedByDescending(WardrobeItem::createdAt)
        ?.toList()
        .orEmpty()
