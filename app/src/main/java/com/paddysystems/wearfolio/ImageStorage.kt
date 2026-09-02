package com.paddysystems.wearfolio

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.paddysystems.wearfolio.data.storage.ProfileStorage
import java.io.File
import java.util.UUID

fun saveImage(
    context: Context,
    profileId: String,
    uri: Uri,
    imageId: String = UUID.randomUUID().toString()
): File? {
    val imageDirectory = File(
        ProfileStorage.profileDirectory(context, profileId),
        "wardrobe_images"
    )

    imageDirectory.mkdirs()

    val imageFile = File(
        imageDirectory,
        "$imageId.jpg"
    )

    return try {
        val copied = context.contentResolver
            .openInputStream(uri)
            ?.use { input ->
                imageFile.outputStream().use { output ->
                    input.copyTo(output)
                }
                true
            }
            ?: false

        if (copied) {
            imageFile
        } else {
            imageFile.delete()
            null
        }
    } catch (exception: Exception) {
        imageFile.delete()
        null
    }
}

fun loadImages(
    context: Context,
    profileId: String
): List<File> {
    val imageDirectory = File(
        ProfileStorage.profileDirectory(context, profileId),
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
