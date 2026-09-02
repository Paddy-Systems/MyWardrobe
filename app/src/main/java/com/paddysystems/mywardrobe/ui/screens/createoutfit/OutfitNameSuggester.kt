package com.paddysystems.mywardrobe.ui.screens.createoutfit

import com.paddysystems.mywardrobe.data.model.OutfitLayer
import com.paddysystems.mywardrobe.data.model.OutfitLayerMode
import com.paddysystems.mywardrobe.data.model.OutfitSlotSelection
import com.paddysystems.mywardrobe.data.model.WardrobeItem
import com.paddysystems.mywardrobe.data.model.defaultClothingTypes
import com.paddysystems.mywardrobe.data.model.wardrobeLabel

private const val MIN_STYLE_SCORE =
    0.015f

fun suggestOutfitName(
    layers: List<OutfitLayer>,
    shoes: OutfitSlotSelection,
    bag: OutfitSlotSelection,
    accessories:
    List<OutfitSlotSelection>,
    wardrobeItems:
    List<WardrobeItem>
): String {

    val itemsById =
        wardrobeItems.associateBy {
            it.id
        }

    val selectedIds =
        buildList {

            layers.forEach { layer ->

                when (layer.mode) {

                    OutfitLayerMode.SEPARATES -> {
                        layer.top.itemId
                            ?.let(::add)

                        layer.bottom.itemId
                            ?.let(::add)
                    }

                    OutfitLayerMode.FULL_LENGTH ->
                        layer
                            .fullLength
                            .itemId
                            ?.let(::add)
                }
            }

            shoes.itemId
                ?.let(::add)

            bag.itemId
                ?.let(::add)

            accessories.forEach {
                it.itemId
                    ?.let(::add)
            }
        }

    val selectedItems =
        selectedIds
            .distinct()
            .mapNotNull {
                itemsById[it]
            }

    if (selectedItems.isEmpty()) {
        return "New fit"
    }

    val dominantColour =
        selectedItems
            .flatMap {
                it.colours
            }
            .groupingBy {
                it
            }
            .eachCount()
            .maxByOrNull {
                it.value
            }
            ?.key

    val dominantStyle =
        selectedItems
            .flatMap {
                it.metadata.styles
            }
            .filter {
                it.similarity >=
                        MIN_STYLE_SCORE
            }
            .groupBy {
                it.id
            }
            .maxByOrNull {
                    (_, tags) ->

                tags.sumOf {
                    it.similarity
                        .toDouble()
                }
            }
            ?.key

    /*
     * Name the outfit primarily from
     * its outermost populated layer.
     */
    val coreLayer =
        layers
            .asReversed()
            .firstOrNull { layer ->

                when (layer.mode) {

                    OutfitLayerMode.SEPARATES ->
                        layer.top.itemId != null ||
                                layer.bottom.itemId != null

                    OutfitLayerMode.FULL_LENGTH ->
                        layer.fullLength
                            .itemId != null
                }
            }

    val coreIds =
        when (
            coreLayer?.mode
        ) {
            OutfitLayerMode.SEPARATES ->
                listOfNotNull(
                    coreLayer.top.itemId,
                    coreLayer.bottom.itemId
                )

            OutfitLayerMode.FULL_LENGTH ->
                listOfNotNull(
                    coreLayer
                        .fullLength
                        .itemId
                )

            null ->
                emptyList()
        }

    val coreNames =
        coreIds
            .mapNotNull {
                itemsById[it]
            }
            .map { item ->

                defaultClothingTypes
                    .firstOrNull {
                        it.id ==
                                item.clothingTypeId
                    }
                    ?.name
                    ?: wardrobeLabel(
                        item.clothingTypeId
                    )
            }

    val coreName =
        when {
            coreNames.isEmpty() ->
                "Fit"

            coreNames.size == 1 ->
                coreNames.first()

            else ->
                coreNames
                    .take(2)
                    .joinToString(
                        " & "
                    )
        }

    val prefix =
        listOfNotNull(
            dominantColour
                ?.let {
                    wardrobeLabel(it)
                },

            dominantStyle
                ?.let {
                    wardrobeLabel(it)
                }
        )

    return (
            prefix +
                    coreName
            )
        .distinctBy {
            it.lowercase()
        }
        .joinToString(" ")
}
