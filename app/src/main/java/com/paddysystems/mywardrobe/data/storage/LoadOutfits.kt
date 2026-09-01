package com.paddysystems.mywardrobe.data.storage

import android.content.Context
import com.paddysystems.mywardrobe.data.model.Outfit
import org.json.JSONObject

fun loadOutfits(context: Context): List<Outfit> =
    OutfitStoragePaths.directory(context)
        .listFiles()
        ?.asSequence()
        ?.filter { it.isFile && it.extension == "json" }
        ?.mapNotNull { runCatching { OutfitJsonCodec.decode(JSONObject(it.readText())) }.getOrNull() }
        ?.sortedByDescending(Outfit::createdAt)
        ?.toList()
        .orEmpty()
