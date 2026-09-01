package com.paddysystems.mywardrobe.domain.additem

import android.content.Context
import android.net.Uri
import android.util.Log
import com.paddysystems.mywardrobe.data.model.WardrobeItem
import com.paddysystems.mywardrobe.data.model.WardrobeMetadata
import com.paddysystems.mywardrobe.data.storage.WardrobeCutoutService
import com.paddysystems.mywardrobe.data.storage.saveWardrobeItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WardrobeItemSaver(private val context: Context) {
    suspend fun save(imageUri: Uri, clothingTypeId: String, colours: List<String>, embedding: FloatArray, metadata: WardrobeMetadata): WardrobeItem? =
        withContext(Dispatchers.IO) {
            val item = saveWardrobeItem(context, imageUri, clothingTypeId, colours, embedding, metadata) ?: return@withContext null
            runCatching { WardrobeCutoutService.ensureCutout(context, item) }
                .onFailure { Log.e("ISNet", "Could not generate cut-out", it) }
                .getOrDefault(item)
        }
}
