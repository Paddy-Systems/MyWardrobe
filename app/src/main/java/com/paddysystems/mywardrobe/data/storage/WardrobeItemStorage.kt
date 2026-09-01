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