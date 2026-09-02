package com.paddysystems.wearfolio.search

import com.paddysystems.wearfolio.data.model.WardrobeItem
import com.paddysystems.wearfolio.data.model.defaultClothingTypes

object WardrobeSortEngine {

    private val clothingTypeNames =
        defaultClothingTypes
            .associate {
                it.id to
                        it.name.lowercase()
            }

    fun sort(
        results: List<WardrobeSearchResult>,
        sortOrder: WardrobeSortOrder,
        hasSearchQuery: Boolean
    ): List<WardrobeItem> {

        /*
         * Default behaviour:
         *
         * no search -> newest
         * searching -> relevance
         */
        if (
            sortOrder ==
            WardrobeSortOrder.AUTO
        ) {
            return if (hasSearchQuery) {

                /*
                 * WardrobeSearchEngine already
                 * returns relevance-ranked results.
                 */
                results.map {
                    it.item
                }

            } else {

                results
                    .map {
                        it.item
                    }
                    .sortedByDescending {
                        it.createdAt
                    }
            }
        }

        val items =
            results.map {
                it.item
            }

        return when (sortOrder) {

            WardrobeSortOrder.NEWEST ->
                items.sortedByDescending {
                    it.createdAt
                }

            WardrobeSortOrder.OLDEST ->
                items.sortedBy {
                    it.createdAt
                }

            WardrobeSortOrder.TYPE ->
                items.sortedWith(
                    compareBy<WardrobeItem> {
                        clothingTypeNames[
                            it.clothingTypeId
                        ]
                            ?: it.clothingTypeId
                    }.thenByDescending {
                        it.createdAt
                    }
                )

            WardrobeSortOrder.COLOUR ->
                items.sortedWith(
                    compareBy<WardrobeItem> {
                        it.colours
                            .minOrNull()
                            .orEmpty()
                    }.thenByDescending {
                        it.createdAt
                    }
                )

            WardrobeSortOrder.AUTO ->
                items
        }
    }
}