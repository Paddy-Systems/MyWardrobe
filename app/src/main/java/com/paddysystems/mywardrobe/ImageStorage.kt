package com.paddysystems.mywardrobe

import android.content.Context
import android.net.Uri
import java.io.File
import java.util.UUID
import androidx.core.content.FileProvider

fun saveImage(context: Context, uri: Uri): File? {
    val imageDirectory = File(
        context.filesDir,
        "wardrobe_images"
    )

    imageDirectory.mkdirs()

    val imageFile = File(
        imageDirectory,
        "${UUID.randomUUID()}.jpg"
    )

    return try {
        context.contentResolver
            .openInputStream(uri)
            ?.use { input ->
                imageFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

        imageFile
    } catch (exception: Exception) {
        null
    }
}

fun loadImages(context: Context): List<File> {
    val imageDirectory = File(
        context.filesDir,
        "wardrobe_images"
    )

    if (!imageDirectory.exists()) {
        return emptyList()
    }

    return imageDirectory
        .listFiles()
        ?.filter { it.isFile }
        ?.sortedByDescending { it.lastModified() }
        ?: emptyList()
}

fun deleteImage(imageFile: File): Boolean {
    return imageFile.delete()
}

fun createCameraImageUri(context: Context): Uri {
    val imageDirectory = File(
        context.cacheDir,
        "camera_images"
    )

    imageDirectory.mkdirs()

    val imageFile = File.createTempFile(
        "wardrobe_",
        ".jpg",
        imageDirectory
    )

    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        imageFile
    )
}