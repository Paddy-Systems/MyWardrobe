package com.paddysystems.mywardrobe.data.model

data class Outfit(
    val id: String,
    val name: String,

    val layers: List<OutfitLayer>,

    val shoes: OutfitSlotSelection =
        OutfitSlotSelection(),

    val bag: OutfitSlotSelection =
        OutfitSlotSelection(),

    val accessories:
    List<OutfitSlotSelection> =
        emptyList(),

    val previewPath: String? = null,

    val createdAt: Long =
        System.currentTimeMillis()
)