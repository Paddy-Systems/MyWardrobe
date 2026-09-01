package com.paddysystems.mywardrobe.data.storage

import android.content.Context
import com.paddysystems.mywardrobe.data.model.Outfit

fun saveOutfit(context: Context, outfit: Outfit): Boolean = runCatching {
    OutfitStoragePaths.outfit(context, outfit.id).writeText(OutfitJsonCodec.encode(outfit).toString())
}.isSuccess
