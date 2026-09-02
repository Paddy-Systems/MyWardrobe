package com.paddysystems.mywardrobe.data.storage

import android.content.Context
import android.net.Uri
import com.paddysystems.mywardrobe.data.model.SemanticTag
import com.paddysystems.mywardrobe.data.model.WardrobeItem
import com.paddysystems.mywardrobe.data.model.WardrobeMetadata
import com.paddysystems.mywardrobe.saveImage
import org.json.JSONArray
import org.json.JSONObject
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File

fun saveWardrobeItem(
    context: Context,
    imageUri: Uri,
    clothingTypeId: String,
    colours: List<String>,
    imageEmbedding: FloatArray,
    metadata: WardrobeMetadata
): WardrobeItem? {
    val imageFile = saveImage(context, imageUri) ?: return null

    val item = WardrobeItem(
        id = imageFile.nameWithoutExtension,
        imagePath = imageFile.absolutePath,
        clothingTypeId = clothingTypeId,
        colours = colours,
        createdAt = System.currentTimeMillis(),
        metadata = metadata
    )

    val itemDirectory = File(context.filesDir, "wardrobe_items")
    itemDirectory.mkdirs()

    val itemFile = File(itemDirectory, "${item.id}.json")

    return try {
        saveWardrobeItemEmbedding(
            context = context,
            itemId = item.id,
            embedding = imageEmbedding
        )

        itemFile.writeText(wardrobeItemToJson(item).toString())
        item
    } catch (exception: Exception) {
        imageFile.delete()
        getEmbeddingFile(context, item.id).delete()
        itemFile.delete()
        null
    }
}

fun loadWardrobeItems(context: Context): List<WardrobeItem> {
    val itemDirectory = File(context.filesDir, "wardrobe_items")

    if (!itemDirectory.exists()) {
        return emptyList()
    }

    return itemDirectory
        .listFiles()
        ?.filter { it.isFile && it.extension == "json" }
        ?.mapNotNull { itemFile ->
            try {
                val item = wardrobeItemFromJson(
                    json = JSONObject(itemFile.readText()),
                    fallbackCreatedAt = itemFile.lastModified()
                )

                item.takeIf { File(it.imagePath).exists() }
            } catch (exception: Exception) {
                null
            }
        }
        ?.sortedByDescending { it.createdAt }
        ?: emptyList()
}

fun deleteWardrobeItem(
    context: Context,
    item: WardrobeItem
): Boolean {
    val outfitsCleaned = removeWardrobeItemFromOutfits(
        context = context,
        itemId = item.id
    )

    if (!outfitsCleaned) {
        return false
    }

    val imageFile = File(item.imagePath)
    val itemFile = File(context.filesDir, "wardrobe_items/${item.id}.json")
    val embeddingFile = getEmbeddingFile(context, item.id)

    val imageDeleted = !imageFile.exists() || imageFile.delete()
    val metadataDeleted = !itemFile.exists() || itemFile.delete()
    val embeddingDeleted = !embeddingFile.exists() || embeddingFile.delete()
    val cutoutDeleted = item.cutoutPath
        ?.let { path ->
            val file = File(path)
            !file.exists() || file.delete()
        }
        ?: true

    return imageDeleted && metadataDeleted && embeddingDeleted && cutoutDeleted
}

fun loadWardrobeItem(
    context: Context,
    itemId: String
): WardrobeItem? {
    val itemFile = File(context.filesDir, "wardrobe_items/$itemId.json")

    if (!itemFile.exists()) {
        return null
    }

    return try {
        val item = wardrobeItemFromJson(
            json = JSONObject(itemFile.readText()),
            fallbackCreatedAt = itemFile.lastModified()
        )

        item.takeIf { File(it.imagePath).exists() }
    } catch (exception: Exception) {
        null
    }
}

fun updateWardrobeItem(
    context: Context,
    item: WardrobeItem,
    clothingTypeId: String,
    colours: List<String>
): Boolean {
    val updatedItem = item.copy(
        clothingTypeId = clothingTypeId,
        colours = colours
    )

    return writeWardrobeItem(context, updatedItem)
}

fun updateWardrobeItemOutfitIds(
    context: Context,
    item: WardrobeItem,
    outfitIds: List<String>
): Boolean {
    val updatedItem = item.copy(
        outfitIds = outfitIds.distinct()
    )

    return writeWardrobeItem(context, updatedItem)
}

fun updateWardrobeItemCutout(
    context: Context,
    item: WardrobeItem,
    cutoutPath: String
): WardrobeItem? {
    val updatedItem = item.copy(cutoutPath = cutoutPath)

    return if (writeWardrobeItem(context, updatedItem)) {
        updatedItem
    } else {
        null
    }
}

private fun writeWardrobeItem(
    context: Context,
    item: WardrobeItem
): Boolean {
    val itemDirectory = File(context.filesDir, "wardrobe_items")
    itemDirectory.mkdirs()

    val itemFile = File(itemDirectory, "${item.id}.json")

    return try {
        itemFile.writeText(wardrobeItemToJson(item).toString())
        true
    } catch (exception: Exception) {
        false
    }
}

private fun wardrobeItemFromJson(
    json: JSONObject,
    fallbackCreatedAt: Long
): WardrobeItem {
    val colours = stringListFromJson(json.optJSONArray("colours"))

    return WardrobeItem(
        id = json.getString("id"),
        imagePath = json.getString("imagePath"),
        cutoutPath = nullableString(json, "cutoutPath"),
        clothingTypeId = json.getString("clothingTypeId"),
        colours = colours,
        createdAt = json.optLong("createdAt", fallbackCreatedAt),
        metadata = metadataFromJson(json.optJSONObject("metadata")),
        outfitIds = stringListFromJson(json.optJSONArray("outfitIds"))
    )
}

private fun stringListFromJson(array: JSONArray?): List<String> {
    if (array == null) {
        return emptyList()
    }

    return List(array.length()) { index ->
        array.optString(index)
    }.filter { it.isNotBlank() }
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

private fun semanticTagsToJson(tags: List<SemanticTag>): JSONArray {
    return JSONArray().apply {
        tags.forEach { tag ->
            put(
                JSONObject()
                    .put("id", tag.id)
                    .put("similarity", tag.similarity.toDouble())
            )
        }
    }
}

private fun semanticTagsFromJson(array: JSONArray?): List<SemanticTag> {
    if (array == null) {
        return emptyList()
    }

    return List(array.length()) { index ->
        val json = array.getJSONObject(index)

        SemanticTag(
            id = json.getString("id"),
            similarity = json.getDouble("similarity").toFloat()
        )
    }
}

private fun metadataToJson(metadata: WardrobeMetadata): JSONObject {
    return JSONObject()
        .put("patterns", semanticTagsToJson(metadata.patterns))
        .put("materials", semanticTagsToJson(metadata.materials))
        .put("styles", semanticTagsToJson(metadata.styles))
        .put("occasions", semanticTagsToJson(metadata.occasions))
        .put("seasons", semanticTagsToJson(metadata.seasons))
        .put("formalities", semanticTagsToJson(metadata.formalities))
}

private fun metadataFromJson(json: JSONObject?): WardrobeMetadata {
    if (json == null) {
        return WardrobeMetadata()
    }

    return WardrobeMetadata(
        patterns = semanticTagsFromJson(json.optJSONArray("patterns")),
        materials = semanticTagsFromJson(json.optJSONArray("materials")),
        styles = semanticTagsFromJson(json.optJSONArray("styles")),
        occasions = semanticTagsFromJson(json.optJSONArray("occasions")),
        seasons = semanticTagsFromJson(json.optJSONArray("seasons")),
        formalities = semanticTagsFromJson(json.optJSONArray("formalities"))
    )
}

private fun wardrobeItemToJson(item: WardrobeItem): JSONObject {
    return JSONObject()
        .put("id", item.id)
        .put("imagePath", item.imagePath)
        .put("clothingTypeId", item.clothingTypeId)
        .put("colours", JSONArray(item.colours))
        .put("createdAt", item.createdAt)
        .put("metadata", metadataToJson(item.metadata))
        .put("cutoutPath", item.cutoutPath ?: JSONObject.NULL)
        .put("outfitIds", JSONArray(item.outfitIds))
}

private fun getEmbeddingFile(
    context: Context,
    itemId: String
): File {
    val directory = File(context.filesDir, "wardrobe_embeddings")
    directory.mkdirs()
    return File(directory, "$itemId.bin")
}

private fun saveWardrobeItemEmbedding(
    context: Context,
    itemId: String,
    embedding: FloatArray
) {
    val file = getEmbeddingFile(context, itemId)

    DataOutputStream(file.outputStream().buffered()).use { output ->
        embedding.forEach { value ->
            output.writeFloat(value)
        }
    }
}

fun loadWardrobeItemEmbedding(
    context: Context,
    itemId: String
): FloatArray? {
    val file = getEmbeddingFile(context, itemId)

    if (!file.exists() || file.length() == 0L || file.length() % 4L != 0L) {
        return null
    }

    val valueCount = (file.length() / 4L).toInt()

    return try {
        DataInputStream(file.inputStream().buffered()).use { input ->
            FloatArray(valueCount) {
                input.readFloat()
            }
        }
    } catch (exception: Exception) {
        null
    }
}
