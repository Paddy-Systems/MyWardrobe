package com.paddysystems.mywardrobe.data.storage

import android.content.Context
import java.io.File

fun loadImages(context: Context): List<File> =
    File(context.filesDir, "wardrobe_images")
        .takeIf(File::exists)
        ?.listFiles()
        ?.filter(File::isFile)
        ?.sortedByDescending(File::lastModified)
        .orEmpty()
