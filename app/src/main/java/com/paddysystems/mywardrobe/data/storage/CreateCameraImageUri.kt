package com.paddysystems.mywardrobe.data.storage

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

fun createCameraImageUri(context: Context): Uri {
    val directory = File(context.cacheDir, "camera_images").apply { mkdirs() }
    val image = File.createTempFile("wardrobe_", ".jpg", directory)
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", image)
}
