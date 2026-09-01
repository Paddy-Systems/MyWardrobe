package com.paddysystems.mywardrobe.data.storage

import android.content.Context
import java.io.File

internal object OutfitStoragePaths {
    fun directory(context: Context) = File(context.filesDir, "outfits").apply { mkdirs() }
    fun outfit(context: Context, outfitId: String) = File(directory(context), "$outfitId.json")
}
