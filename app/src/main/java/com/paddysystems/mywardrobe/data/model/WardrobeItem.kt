package com.paddysystems.mywardrobe.data.model

data class WardrobeItem(
    val id: String,
    val imagePath: String,
    val clothingTypeId: String,
    val colours: List<String> = emptyList(),
    val events: List<WardrobeEvent> = emptyList(),
    val outfitIds: List<String> = emptyList()
)

fun WardrobeItem.isValid(): Boolean {
    return imagePath.isNotBlank() &&
            clothingTypeId.isNotBlank() &&
            colours.size in 1..3
}