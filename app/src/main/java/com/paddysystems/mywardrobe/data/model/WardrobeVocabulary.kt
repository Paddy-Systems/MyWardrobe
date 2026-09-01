package com.paddysystems.mywardrobe.data.model

val wardrobeColours = listOf(
    "black",
    "white",
    "grey",
    "blue",
    "navy",
    "red",
    "green",
    "yellow",
    "orange",
    "pink",
    "purple",
    "brown",
    "beige",
    "cream",
    "teal"
)

val wardrobePatterns = listOf(
    "solid",
    "striped",
    "checked",
    "plaid",
    "floral",
    "graphic",
    "polka_dot",
    "animal_print",
    "camouflage",
    "abstract"
)

val wardrobeMaterials = listOf(
    "denim",
    "cotton",
    "wool",
    "leather",
    "suede",
    "knit",
    "linen",
    "satin",
    "silk",
    "fleece",
    "velvet"
)

val wardrobeStyles = listOf(
    "casual",
    "smart",
    "formal",
    "sporty",
    "streetwear",
    "vintage",
    "minimalist",
    "party",
    "workwear",
    "outdoors"
)

val wardrobeOccasions = listOf(
    "everyday",
    "work",
    "evening",
    "party",
    "wedding",
    "gym",
    "sports",
    "holiday",
    "beach",
    "outdoors"
)

val wardrobeSeasons = listOf(
    "spring",
    "summer",
    "autumn",
    "winter"
)

val wardrobeFormalities = listOf(
    "casual",
    "smart_casual",
    "formal"
)

fun wardrobeLabel(
    id: String
): String {
    return id
        .replace("_", " ")
        .replaceFirstChar {
            it.uppercase()
        }
}