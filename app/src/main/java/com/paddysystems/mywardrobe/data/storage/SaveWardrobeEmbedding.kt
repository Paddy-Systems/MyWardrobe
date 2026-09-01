package com.paddysystems.mywardrobe.data.storage

import android.content.Context
import java.io.DataOutputStream

internal fun saveWardrobeEmbedding(context: Context, itemId: String, embedding: FloatArray) {
    DataOutputStream(WardrobeStoragePaths.embedding(context, itemId).outputStream().buffered()).use { output ->
        embedding.forEach(output::writeFloat)
    }
}
