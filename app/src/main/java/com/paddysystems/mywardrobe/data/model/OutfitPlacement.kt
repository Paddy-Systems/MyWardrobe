package com.paddysystems.mywardrobe.data.model

enum class OutfitPlacement {
    TOP,
    BOTTOM,
    FULL_LENGTH,
    SHOES,
    BAG,
    ACCESSORY
}

fun WardrobeItem.outfitPlacement():
        OutfitPlacement? {

    return when (clothingTypeId) {

        // Tops
        "tshirt",
        "shirt",
        "blouse",
        "vest_top",
        "bodysuit",
        "jumper",
        "hoodie",
        "cardigan",
        "jacket",
        "blazer",
        "bra" ->
            OutfitPlacement.TOP

        // Bottoms
        "jeans",
        "trousers",
        "leggings",
        "joggers",
        "shorts",
        "skirt",
        "briefs",
        "boxer_shorts",
        "tights" ->
            OutfitPlacement.BOTTOM

        // One complete body layer
        "dress",
        "jumpsuit",
        "playsuit",
        "tracksuit",
        "pyjamas",
        "swimsuit",
        "lingerie_set",
        "coat" ->
            OutfitPlacement.FULL_LENGTH

        // Footwear
        "trainers",
        "shoes",
        "boots",
        "sandals",
        "heels" ->
            OutfitPlacement.SHOES

        // Bags
        "handbag",
        "backpack",
        "clutch" ->
            OutfitPlacement.BAG

        // Accessories
        "belt",
        "hat",
        "cap",
        "scarf",
        "gloves",
        "watch",
        "sunglasses",
        "jewellery",
        "socks" ->
            OutfitPlacement.ACCESSORY

        else -> null
    }
}