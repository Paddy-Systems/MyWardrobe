package com.paddysystems.mywardrobe.data.storage

import android.content.Context
import java.io.DataInputStream

fun loadWardrobeItemEmbedding(context: Context, itemId: String): FloatArray? {
    val file = WardrobeStoragePaths.embedding(context, itemId)
    if (!file.exists() || file.length() == 0L || file.length() % Float.SIZE_BYTES != 0L) return null
    return runCatching {
        DataInputStream(file.inputStream().buffered()).use { input ->
            FloatArray((file.length() / Float.SIZE_BYTES).toInt()) { input.readFloat() }
        }
    }.getOrNull()
}
