package com.paddysystems.mywardrobe.data.model

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
