package com.paddysystems.mywardrobe.data.storage

import android.content.Context
import android.net.Uri
import com.paddysystems.mywardrobe.data.model.WardrobeItem
import com.paddysystems.mywardrobe.saveImage
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

fun saveWardrobeItem(
    context: Context,
    imageUri: Uri,
    clothingTypeId: String,
    colours: List<String>
): WardrobeItem? {
    val imageFile = saveImage(
        context,
        imageUri
    ) ?: return null

    val item = WardrobeItem(
        id = imageFile.nameWithoutExtension,
        imagePath = imageFile.absolutePath,
        clothingTypeId = clothingTypeId,
        colours = colours
    )

    val itemDirectory = File(
        context.filesDir,
        "wardrobe_items"
    )

    itemDirectory.mkdirs()

    val itemFile = File(
        itemDirectory,
        "${item.id}.json"
    )

    return try {
        val json = JSONObject()
            .put("id", item.id)
            .put("imagePath", item.imagePath)
            .put("clothingTypeId", item.clothingTypeId)
            .put(
                "colours",
                JSONArray(item.colours)
            )

        itemFile.writeText(
            json.toString()
        )

        item
    } catch (exception: Exception) {
        imageFile.delete()
        null
    }
}

fun loadWardrobeItems(
    context: Context
): List<WardrobeItem> {
    val itemDirectory = File(
        context.filesDir,
        "wardrobe_items"
    )

    if (!itemDirectory.exists()) {
        return emptyList()
    }

    return itemDirectory
        .listFiles()
        ?.filter {
            it.isFile &&
                    it.extension == "json"
        }
        ?.sortedByDescending {
            it.lastModified()
        }
        ?.mapNotNull { itemFile ->
            try {
                val json = JSONObject(
                    itemFile.readText()
                )

                val colourArray =
                    json.getJSONArray("colours")

                val colours = List(
                    colourArray.length()
                ) { index ->
                    colourArray.getString(index)
                }

                val item = WardrobeItem(
                    id = json.getString("id"),
                    imagePath = json.getString("imagePath"),
                    clothingTypeId = json.getString(
                        "clothingTypeId"
                    ),
                    colours = colours
                )

                if (
                    File(item.imagePath).exists()
                ) {
                    item
                } else {
                    null
                }
            } catch (exception: Exception) {
                null
            }
        }
        ?: emptyList()
}

fun deleteWardrobeItem(
    context: Context,
    item: WardrobeItem
): Boolean {
    val imageFile = File(
        item.imagePath
    )

    val itemFile = File(
        context.filesDir,
        "wardrobe_items/${item.id}.json"
    )

    val imageDeleted =
        !imageFile.exists() ||
                imageFile.delete()

    val metadataDeleted =
        !itemFile.exists() ||
                itemFile.delete()

    return imageDeleted &&
            metadataDeleted
}

fun loadWardrobeItem(
    context: Context,
    itemId: String
): WardrobeItem? {
    val itemFile = File(
        context.filesDir,
        "wardrobe_items/$itemId.json"
    )

    if (!itemFile.exists()) {
        return null
    }

    return try {
        val json = JSONObject(
            itemFile.readText()
        )

        val colourArray =
            json.getJSONArray("colours")

        val colours = List(
            colourArray.length()
        ) { index ->
            colourArray.getString(index)
        }

        val item = WardrobeItem(
            id = json.getString("id"),
            imagePath = json.getString("imagePath"),
            clothingTypeId = json.getString(
                "clothingTypeId"
            ),
            colours = colours
        )

        if (
            File(item.imagePath).exists()
        ) {
            item
        } else {
            null
        }
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

    val itemFile = File(
        context.filesDir,
        "wardrobe_items/${item.id}.json"
    )

    return try {
        val json = JSONObject()
            .put("id", updatedItem.id)
            .put("imagePath", updatedItem.imagePath)
            .put(
                "clothingTypeId",
                updatedItem.clothingTypeId
            )
            .put(
                "colours",
                JSONArray(updatedItem.colours)
            )

        itemFile.writeText(
            json.toString()
        )

        true
    } catch (exception: Exception) {
        false
    }
}