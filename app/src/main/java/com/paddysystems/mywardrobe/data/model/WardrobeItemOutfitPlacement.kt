package com.paddysystems.mywardrobe.data.model

fun WardrobeItem.outfitPlacement(): OutfitPlacement? = when (clothingTypeId) {
    "tshirt", "shirt", "blouse", "vest_top", "bodysuit", "jumper", "hoodie",
    "cardigan", "jacket", "blazer", "bra" -> OutfitPlacement.TOP
    "jeans", "trousers", "leggings", "joggers", "shorts", "skirt", "briefs",
    "boxer_shorts", "tights" -> OutfitPlacement.BOTTOM
    "dress", "jumpsuit", "playsuit", "tracksuit", "pyjamas", "swimsuit",
    "lingerie_set", "coat" -> OutfitPlacement.FULL_LENGTH
    "trainers", "shoes", "boots", "sandals", "heels" -> OutfitPlacement.SHOES
    "handbag", "backpack", "clutch" -> OutfitPlacement.BAG
    "belt", "hat", "cap", "scarf", "gloves", "watch", "sunglasses",
    "jewellery", "socks" -> OutfitPlacement.ACCESSORY
    else -> null
}
