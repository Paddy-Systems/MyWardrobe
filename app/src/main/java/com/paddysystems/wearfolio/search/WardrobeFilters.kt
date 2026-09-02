package com.paddysystems.wearfolio.search

data class WardrobeFilters(
    val clothingTypeIds: Set<String> =
        emptySet(),

    val colours: Set<String> =
        emptySet(),

    val patterns: Set<String> =
        emptySet(),

    val materials: Set<String> =
        emptySet(),

    val styles: Set<String> =
        emptySet(),

    val occasions: Set<String> =
        emptySet(),

    val seasons: Set<String> =
        emptySet(),

    val formalities: Set<String> =
        emptySet()
) {

    val activeCount: Int
        get() =
            clothingTypeIds.size +
                    colours.size +
                    patterns.size +
                    materials.size +
                    styles.size +
                    occasions.size +
                    seasons.size +
                    formalities.size

    val isEmpty: Boolean
        get() =
            activeCount == 0
}