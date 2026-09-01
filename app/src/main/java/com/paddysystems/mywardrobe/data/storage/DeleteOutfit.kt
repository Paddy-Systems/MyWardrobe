package com.paddysystems.mywardrobe.data.storage

import android.content.Context

fun deleteOutfit(context: Context, outfitId: String): Boolean =
    OutfitStoragePaths.outfit(context, outfitId).let { !it.exists() || it.delete() }
