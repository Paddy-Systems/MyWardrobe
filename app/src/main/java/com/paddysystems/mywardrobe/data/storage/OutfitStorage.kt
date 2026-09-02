package com.paddysystems.mywardrobe.data.storage

import android.content.Context
import com.paddysystems.mywardrobe.data.model.Outfit
import com.paddysystems.mywardrobe.data.model.OutfitLayer
import com.paddysystems.mywardrobe.data.model.OutfitLayerMode
import com.paddysystems.mywardrobe.data.model.OutfitSlotSelection
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

fun saveOutfit(
    context: Context,
    outfit: Outfit
): Boolean {
    val directory = File(context.filesDir, "outfits")
    directory.mkdirs()

    val file = File(directory, "${outfit.id}.json")

    return try {
        file.writeText(outfitToJson(outfit).toString())

        // outfitIds is cached relationship metadata. The outfit file is
        // still considered successfully saved if this repair pass fails.
        runCatching {
            syncWardrobeOutfitIds(context)
        }
        true
    } catch (exception: Exception) {
        false
    }
}

fun loadOutfits(context: Context): List<Outfit> {
    val directory = File(context.filesDir, "outfits")

    if (!directory.exists()) {
        return emptyList()
    }

    return directory
        .listFiles()
        ?.filter { it.isFile && it.extension == "json" }
        ?.mapNotNull { file ->
            try {
                outfitFromJson(JSONObject(file.readText()))
            } catch (exception: Exception) {
                null
            }
        }
        ?.sortedByDescending { it.createdAt }
        ?: emptyList()
}

fun loadOutfit(
    context: Context,
    outfitId: String
): Outfit? {
    val file = File(context.filesDir, "outfits/$outfitId.json")

    if (!file.exists()) {
        return null
    }

    return try {
        outfitFromJson(JSONObject(file.readText()))
    } catch (exception: Exception) {
        null
    }
}

fun renameOutfit(
    context: Context,
    outfitId: String,
    name: String
): Outfit? {
    val existing = loadOutfit(context, outfitId) ?: return null
    val updated = existing.copy(name = name.trim())

    return if (saveOutfit(context, updated)) updated else null
}

fun deleteOutfit(
    context: Context,
    outfitId: String
): Boolean {
    val file = File(context.filesDir, "outfits/$outfitId.json")
    val deleted = !file.exists() || file.delete()

    if (deleted) {
        runCatching {
            syncWardrobeOutfitIds(context)
        }
    }

    return deleted
}

private fun outfitToJson(outfit: Outfit): JSONObject {
    return JSONObject()
        .put("id", outfit.id)
        .put("name", outfit.name)
        .put("createdAt", outfit.createdAt)
        .put("previewPath", outfit.previewPath ?: JSONObject.NULL)
        .put(
            "layers",
            JSONArray().apply {
                outfit.layers.forEach { put(layerToJson(it)) }
            }
        )
        .put("shoes", slotToJson(outfit.shoes))
        .put("bag", slotToJson(outfit.bag))
        .put(
            "accessories",
            JSONArray().apply {
                outfit.accessories.forEach { put(slotToJson(it)) }
            }
        )
}

private fun outfitFromJson(json: JSONObject): Outfit {
    val layersArray = json.optJSONArray("layers")
    val layers = if (layersArray == null) {
        emptyList()
    } else {
        List(layersArray.length()) { index ->
            layerFromJson(layersArray.getJSONObject(index))
        }
    }

    val accessoriesArray = json.optJSONArray("accessories")
    val accessories = if (accessoriesArray == null) {
        emptyList()
    } else {
        List(accessoriesArray.length()) { index ->
            slotFromJson(accessoriesArray.getJSONObject(index))
        }
    }

    return Outfit(
        id = json.getString("id"),
        name = json.getString("name"),
        layers = layers,
        shoes = json.optJSONObject("shoes")?.let(::slotFromJson) ?: OutfitSlotSelection(),
        bag = json.optJSONObject("bag")?.let(::slotFromJson) ?: OutfitSlotSelection(),
        accessories = accessories,
        previewPath = nullableString(json, "previewPath"),
        createdAt = json.optLong("createdAt", 0L)
    )
}

private fun layerToJson(layer: OutfitLayer): JSONObject {
    return JSONObject()
        .put("id", layer.id)
        .put("mode", layer.mode.name)
        .put("top", slotToJson(layer.top))
        .put("bottom", slotToJson(layer.bottom))
        .put("fullLength", slotToJson(layer.fullLength))
}

private fun layerFromJson(json: JSONObject): OutfitLayer {
    val mode = runCatching {
        OutfitLayerMode.valueOf(json.optString("mode"))
    }.getOrDefault(OutfitLayerMode.SEPARATES)

    return OutfitLayer(
        id = json.getString("id"),
        mode = mode,
        top = json.optJSONObject("top")?.let(::slotFromJson) ?: OutfitSlotSelection(),
        bottom = json.optJSONObject("bottom")?.let(::slotFromJson) ?: OutfitSlotSelection(),
        fullLength = json.optJSONObject("fullLength")?.let(::slotFromJson) ?: OutfitSlotSelection()
    )
}

private fun slotToJson(slot: OutfitSlotSelection): JSONObject {
    return JSONObject()
        .put("itemId", slot.itemId ?: JSONObject.NULL)
        .put("isLocked", slot.isLocked)
}

private fun slotFromJson(json: JSONObject): OutfitSlotSelection {
    return OutfitSlotSelection(
        itemId = nullableString(json, "itemId"),
        isLocked = json.optBoolean("isLocked", false)
    )
}

private fun nullableString(
    json: JSONObject,
    key: String
): String? {
    if (json.isNull(key)) {
        return null
    }

    return json.optString(key).takeIf { it.isNotBlank() }
}
