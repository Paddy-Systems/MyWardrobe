package com.paddysystems.mywardrobe.data.storage

import android.content.Context
import android.net.Uri
import java.io.File
import java.util.UUID

fun saveImage(context: Context, uri: Uri): File? {
    val directory = File(context.filesDir, "wardrobe_images").apply { mkdirs() }
    val target = File(directory, "${UUID.randomUUID()}.jpg")
    return runCatching {
        context.contentResolver.openInputStream(uri)?.use { input ->
            target.outputStream().use(input::copyTo)
        } ?: error("Unable to open image URI")
        target
    }.getOrNull()
}
