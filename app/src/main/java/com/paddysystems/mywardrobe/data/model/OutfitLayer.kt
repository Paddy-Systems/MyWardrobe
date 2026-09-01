package com.paddysystems.mywardrobe.data.model

enum class OutfitLayerMode {
    SEPARATES,
    FULL_LENGTH
}

data class OutfitLayer(
    val id: String,

    val mode: OutfitLayerMode =
        OutfitLayerMode.SEPARATES,

    val top: OutfitSlotSelection =
        OutfitSlotSelection(),

    val bottom: OutfitSlotSelection =
        OutfitSlotSelection(),

    val fullLength:
    OutfitSlotSelection =
        OutfitSlotSelection()
)