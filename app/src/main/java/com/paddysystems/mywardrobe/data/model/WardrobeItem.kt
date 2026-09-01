package com.paddysystems.mywardrobe.data.model

data class WardrobeItem(
    val id: String,
    val imagePath: String,
    val cutoutPath: String? = null,

    val clothingTypeId: String,
    val colours: List<String> = emptyList(),

    val createdAt: Long = 0L,

    val metadata: WardrobeMetadata =
        WardrobeMetadata(),

    val events: List<WardrobeEvent> =
        emptyList(),

    val outfitIds: List<String> =
        emptyList()
)
