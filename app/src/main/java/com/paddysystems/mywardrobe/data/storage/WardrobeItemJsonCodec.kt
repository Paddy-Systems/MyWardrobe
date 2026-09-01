package com.paddysystems.mywardrobe.data.storage

import com.paddysystems.mywardrobe.data.model.SemanticTag
import com.paddysystems.mywardrobe.data.model.WardrobeItem
import com.paddysystems.mywardrobe.data.model.WardrobeMetadata
import org.json.JSONArray
import org.json.JSONObject

internal object WardrobeItemJsonCodec {
    fun encode(item: WardrobeItem): JSONObject = JSONObject()
        .put("id", item.id)
        .put("imagePath", item.imagePath)
        .put("clothingTypeId", item.clothingTypeId)
        .put("colours", JSONArray(item.colours))
        .put("createdAt", item.createdAt)
        .put("metadata", encodeMetadata(item.metadata))
        .put("cutoutPath", item.cutoutPath ?: JSONObject.NULL)

    fun decode(json: JSONObject, fallbackCreatedAt: Long = 0L): WardrobeItem = WardrobeItem(
        id = json.getString("id"),
        imagePath = json.getString("imagePath"),
        clothingTypeId = json.getString("clothingTypeId"),
        colours = json.getJSONArray("colours").toStringList(),
        createdAt = json.optLong("createdAt", fallbackCreatedAt),
        metadata = decodeMetadata(json.optJSONObject("metadata")),
        cutoutPath = json.nullableString("cutoutPath")
    )

    private fun encodeMetadata(metadata: WardrobeMetadata) = JSONObject()
        .put("patterns", encodeTags(metadata.patterns))
        .put("materials", encodeTags(metadata.materials))
        .put("styles", encodeTags(metadata.styles))
        .put("occasions", encodeTags(metadata.occasions))
        .put("seasons", encodeTags(metadata.seasons))
        .put("formalities", encodeTags(metadata.formalities))

    private fun decodeMetadata(json: JSONObject?): WardrobeMetadata =
        json?.let {
            WardrobeMetadata(
                patterns = decodeTags(it.optJSONArray("patterns")),
                materials = decodeTags(it.optJSONArray("materials")),
                styles = decodeTags(it.optJSONArray("styles")),
                occasions = decodeTags(it.optJSONArray("occasions")),
                seasons = decodeTags(it.optJSONArray("seasons")),
                formalities = decodeTags(it.optJSONArray("formalities"))
            )
        } ?: WardrobeMetadata()

    private fun encodeTags(tags: List<SemanticTag>) = JSONArray().apply {
        tags.forEach { put(JSONObject().put("id", it.id).put("similarity", it.similarity.toDouble())) }
    }

    private fun decodeTags(array: JSONArray?): List<SemanticTag> =
        array?.let { values ->
            List(values.length()) { index ->
                values.getJSONObject(index).let { SemanticTag(it.getString("id"), it.getDouble("similarity").toFloat()) }
            }
        }.orEmpty()

    private fun JSONArray.toStringList() = List(length(), ::getString)

    private fun JSONObject.nullableString(key: String): String? =
        takeUnless { isNull(key) }?.optString(key)?.takeIf(String::isNotBlank)
}
