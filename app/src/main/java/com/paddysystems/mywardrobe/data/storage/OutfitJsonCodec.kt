package com.paddysystems.mywardrobe.data.storage

import com.paddysystems.mywardrobe.data.model.Outfit
import com.paddysystems.mywardrobe.data.model.OutfitLayer
import com.paddysystems.mywardrobe.data.model.OutfitLayerMode
import com.paddysystems.mywardrobe.data.model.OutfitSlotSelection
import org.json.JSONArray
import org.json.JSONObject

internal object OutfitJsonCodec {
    fun encode(outfit: Outfit): JSONObject = JSONObject()
        .put("id", outfit.id)
        .put("name", outfit.name)
        .put("createdAt", outfit.createdAt)
        .put("previewPath", outfit.previewPath ?: JSONObject.NULL)
        .put("layers", JSONArray().apply { outfit.layers.forEach { put(encodeLayer(it)) } })
        .put("shoes", encodeSlot(outfit.shoes))
        .put("bag", encodeSlot(outfit.bag))
        .put("accessories", JSONArray().apply { outfit.accessories.forEach { put(encodeSlot(it)) } })

    fun decode(json: JSONObject): Outfit = Outfit(
        id = json.getString("id"),
        name = json.getString("name"),
        layers = json.optJSONArray("layers").mapObjects(::decodeLayer),
        shoes = json.optJSONObject("shoes")?.let(::decodeSlot) ?: OutfitSlotSelection(),
        bag = json.optJSONObject("bag")?.let(::decodeSlot) ?: OutfitSlotSelection(),
        accessories = json.optJSONArray("accessories").mapObjects(::decodeSlot),
        previewPath = json.nullableString("previewPath"),
        createdAt = json.optLong("createdAt", 0L)
    )

    private fun encodeLayer(layer: OutfitLayer) = JSONObject()
        .put("id", layer.id)
        .put("mode", layer.mode.name)
        .put("top", encodeSlot(layer.top))
        .put("bottom", encodeSlot(layer.bottom))
        .put("fullLength", encodeSlot(layer.fullLength))

    private fun decodeLayer(json: JSONObject) = OutfitLayer(
        id = json.getString("id"),
        mode = runCatching { OutfitLayerMode.valueOf(json.optString("mode")) }.getOrDefault(OutfitLayerMode.SEPARATES),
        top = json.optJSONObject("top")?.let(::decodeSlot) ?: OutfitSlotSelection(),
        bottom = json.optJSONObject("bottom")?.let(::decodeSlot) ?: OutfitSlotSelection(),
        fullLength = json.optJSONObject("fullLength")?.let(::decodeSlot) ?: OutfitSlotSelection()
    )

    private fun encodeSlot(slot: OutfitSlotSelection) = JSONObject()
        .put("itemId", slot.itemId ?: JSONObject.NULL)
        .put("isLocked", slot.isLocked)

    private fun decodeSlot(json: JSONObject) = OutfitSlotSelection(
        itemId = json.nullableString("itemId"),
        isLocked = json.optBoolean("isLocked", false)
    )

    private fun <T> JSONArray?.mapObjects(transform: (JSONObject) -> T): List<T> =
        this?.let { array -> List(array.length()) { transform(array.getJSONObject(it)) } }.orEmpty()

    private fun JSONObject.nullableString(key: String): String? =
        takeUnless { isNull(key) }?.optString(key)?.takeIf(String::isNotBlank)
}
