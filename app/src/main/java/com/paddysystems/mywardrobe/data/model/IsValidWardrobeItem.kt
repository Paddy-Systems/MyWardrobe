package com.paddysystems.mywardrobe.data.model

fun WardrobeItem.isValid(): Boolean =
    imagePath.isNotBlank() && clothingTypeId.isNotBlank() && colours.size in 1..3
