package com.paddysystems.mywardrobe.data.storage

import android.content.Context
import java.io.File

internal object WardrobeStoragePaths {
    fun itemDirectory(context: Context) = File(context.filesDir, "wardrobe_items").apply { mkdirs() }
    fun item(context: Context, itemId: String) = File(itemDirectory(context), "$itemId.json")
    fun embeddingDirectory(context: Context) = File(context.filesDir, "wardrobe_embeddings").apply { mkdirs() }
    fun embedding(context: Context, itemId: String) = File(embeddingDirectory(context), "$itemId.bin")
}
