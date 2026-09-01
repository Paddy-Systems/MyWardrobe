package com.paddysystems.mywardrobe.data.storage

import android.content.Context
import com.paddysystems.mywardrobe.data.model.WardrobeItem
import java.io.File

fun deleteWardrobeItem(context: Context, item: WardrobeItem): Boolean {
    val files = listOfNotNull(
        File(item.imagePath),
        WardrobeStoragePaths.item(context, item.id),
        WardrobeStoragePaths.embedding(context, item.id),
        item.cutoutPath?.let(::File)
    )
    return files.all { !it.exists() || it.delete() }
}
