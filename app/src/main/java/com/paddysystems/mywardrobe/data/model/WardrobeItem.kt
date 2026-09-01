package com.paddysystems.mywardrobe.data.model

data class WardrobeItem(
    val id: String,
    val imagePath: String,
    val cropType: CropType,
    val clothingTypeId: String,
    val colours: List<String> = emptyList(),
    val brand: String? = null,
    val size: String? = null,
    val events: List<WardrobeEvent> = emptyList(),
    val outfitIds: List<String> = emptyList()
)

enum class CropType(
    val aspectRatio: Float
) {
    FULL_BODY(0.5f),
    HALF_PIECE(1f)
}

fun WardrobeItem.isValid(): Boolean {
    return imagePath.isNotBlank() &&
            clothingTypeId.isNotBlank() &&
            colours.size in 1..3
}