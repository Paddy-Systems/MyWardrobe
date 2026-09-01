package com.paddysystems.mywardrobe.data.storage

import android.content.Context
import android.net.Uri
import com.paddysystems.mywardrobe.data.model.WardrobeItem
import com.paddysystems.mywardrobe.data.model.WardrobeMetadata

fun saveWardrobeItem(
    context: Context,
    imageUri: Uri,
    clothingTypeId: String,
    colours: List<String>,
    imageEmbedding: FloatArray,
    metadata: WardrobeMetadata
): WardrobeItem? {
    val image = saveImage(context, imageUri) ?: return null
    val item = WardrobeItem(
        id = image.nameWithoutExtension,
        imagePath = image.absolutePath,
        clothingTypeId = clothingTypeId,
        colours = colours,
        createdAt = System.currentTimeMillis(),
        metadata = metadata
    )
    val itemFile = WardrobeStoragePaths.item(context, item.id)
    val embeddingFile = WardrobeStoragePaths.embedding(context, item.id)

    return runCatching {
        saveWardrobeEmbedding(context, item.id, imageEmbedding)
        itemFile.writeText(WardrobeItemJsonCodec.encode(item).toString())
        item
    }.getOrElse {
        image.delete()
        embeddingFile.delete()
        itemFile.delete()
        null
    }
}
