package com.paddysystems.mywardrobe.data.model


val defaultClothingTypes = listOf(
    // Tops
    ClothingType("tshirt", "T-shirt", true),
    ClothingType("shirt", "Shirt", true),
    ClothingType("blouse", "Blouse", true),
    ClothingType("vest_top", "Vest top", true),
    ClothingType("bodysuit", "Bodysuit", true),

    // Knitwear
    ClothingType("jumper", "Jumper", true),
    ClothingType("hoodie", "Hoodie", true),
    ClothingType("cardigan", "Cardigan", true),

    // Outerwear
    ClothingType("jacket", "Jacket", true),
    ClothingType("blazer", "Blazer", true),
    ClothingType("coat", "Coat", true),

    // Bottoms
    ClothingType("jeans", "Jeans", true),
    ClothingType("trousers", "Trousers", true),
    ClothingType("leggings", "Leggings", true),
    ClothingType("joggers", "Joggers", true),
    ClothingType("shorts", "Shorts", true),
    ClothingType("skirt", "Skirt", true),

    // One-piece
    ClothingType("dress", "Dress", true),
    ClothingType("jumpsuit", "Jumpsuit", true),
    ClothingType("playsuit", "Playsuit", true),

    // Other clothing
    ClothingType("tracksuit", "Tracksuit", true),
    ClothingType("pyjamas", "Pyjamas", true),
    ClothingType("swimsuit", "Swimsuit", true),

    // Footwear
    ClothingType("trainers", "Trainers", true),
    ClothingType("shoes", "Shoes", true),
    ClothingType("boots", "Boots", true),
    ClothingType("sandals", "Sandals", true),
    ClothingType("heels", "Heels", true),

    // Underwear
    ClothingType(
        "bra",
        "Bra",
        true
    ),
    ClothingType(
        "briefs",
        "Briefs",
        true
    ),
    ClothingType(
        "boxer_shorts",
        "Boxer shorts",
        true
    ),
    ClothingType(
        "lingerie_set",
        "Lingerie set",
        true
    ),
    ClothingType(
        "tights",
        "Tights",
        true
    ),

// Bags
    ClothingType(
        "handbag",
        "Handbag",
        true
    ),
    ClothingType(
        "backpack",
        "Backpack",
        true
    ),
    ClothingType(
        "clutch",
        "Clutch bag",
        true
    ),

// Accessories
    ClothingType(
        "belt",
        "Belt",
        true
    ),
    ClothingType(
        "hat",
        "Hat",
        true
    ),
    ClothingType(
        "cap",
        "Cap",
        true
    ),
    ClothingType(
        "scarf",
        "Scarf",
        true
    ),
    ClothingType(
        "gloves",
        "Gloves",
        true
    ),
    ClothingType(
        "watch",
        "Watch",
        true
    ),
    ClothingType(
        "sunglasses",
        "Sunglasses",
        true
    ),
    ClothingType(
        "jewellery",
        "Jewellery",
        true
    ),
    ClothingType(
        "socks",
        "Socks",
        true
    ),

    // Manual fallback — the model doesn't predict this
    ClothingType("other", "Other", true)
)