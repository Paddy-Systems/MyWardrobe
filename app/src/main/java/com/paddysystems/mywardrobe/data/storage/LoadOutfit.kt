package com.paddysystems.mywardrobe.data.storage

import android.content.Context
import com.paddysystems.mywardrobe.data.model.Outfit
import org.json.JSONObject
import java.io.File

fun loadOutfit(context: Context, outfitId: String): Outfit? {
    val file = OutfitStoragePaths.outfit(context, outfitId).takeIf(File::exists) ?: return null
    return runCatching { OutfitJsonCodec.decode(JSONObject(file.readText())) }.getOrNull()
}
