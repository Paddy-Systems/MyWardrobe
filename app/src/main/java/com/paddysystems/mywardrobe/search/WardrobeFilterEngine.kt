package com.paddysystems.mywardrobe.search

import com.paddysystems.mywardrobe.data.model.WardrobeItem

object WardrobeFilterEngine {

    fun filter(
        items: List<WardrobeItem>,
        filters: WardrobeFilters
    ): List<WardrobeItem> {

        if (filters.isEmpty) {
            return items
        }

        return items.filter { item ->

            matchesClothingType(
                item,
                filters
            ) &&
                    matchesColours(
                        item,
                        filters
                    ) &&
                    WardrobeSemanticSignals
                        .matchesAny(
                            item.metadata.patterns,
                            filters.patterns
                        ) &&
                    WardrobeSemanticSignals
                        .matchesAny(
                            item.metadata.materials,
                            filters.materials
                        ) &&
                    WardrobeSemanticSignals
                        .matchesAny(
                            item.metadata.styles,
                            filters.styles
                        ) &&
                    WardrobeSemanticSignals
                        .matchesAny(
                            item.metadata.occasions,
                            filters.occasions
                        ) &&
                    WardrobeSemanticSignals
                        .matchesAny(
                            item.metadata.seasons,
                            filters.seasons
                        ) &&
                    WardrobeSemanticSignals
                        .matchesAny(
                            item.metadata.formalities,
                            filters.formalities
                        )
        }
    }

    private fun matchesClothingType(
        item: WardrobeItem,
        filters: WardrobeFilters
    ): Boolean {

        if (
            filters.clothingTypeIds.isEmpty()
        ) {
            return true
        }

        return item.clothingTypeId in
                filters.clothingTypeIds
    }

    private fun matchesColours(
        item: WardrobeItem,
        filters: WardrobeFilters
    ): Boolean {

        if (
            filters.colours.isEmpty()
        ) {
            return true
        }

        return item.colours.any {
            it in filters.colours
        }
    }
}