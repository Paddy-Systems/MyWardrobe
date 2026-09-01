package com.paddysystems.mywardrobe.data.storage

import java.io.File

fun deleteImage(imageFile: File): Boolean = imageFile.delete()
