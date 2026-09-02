package com.paddysystems.wearfolio.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paddysystems.wearfolio.data.model.defaultClothingTypes
import com.paddysystems.wearfolio.data.model.wardrobeLabel
import com.paddysystems.wearfolio.search.WardrobeFilters
import com.paddysystems.wearfolio.search.WardrobeSortOrder

@Composable
fun WardrobeActiveControls(
    filters: WardrobeFilters,
    sortOrder: WardrobeSortOrder,
    hasSearchQuery: Boolean,
    onFiltersChange:
        (WardrobeFilters) -> Unit,
    onSortReset: () -> Unit,
    onResetAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (
        filters.isEmpty &&
        sortOrder ==
        WardrobeSortOrder.AUTO &&
        !hasSearchQuery
    ) {
        return
    }

    val clothingTypeNames =
        remember {
            defaultClothingTypes
                .associate {
                    it.id to it.name
                }
        }

    LazyRow(
        modifier = modifier,
        horizontalArrangement =
            Arrangement.spacedBy(
                8.dp
            ),
        contentPadding =
            PaddingValues(
                horizontal = 2.dp
            )
    ) {

        filters.clothingTypeIds
            .sorted()
            .forEach { id ->

                item(
                    key = "type-$id"
                ) {
                    ActiveControlChip(
                        label =
                            "Type: ${
                                clothingTypeNames[id]
                                    ?: wardrobeLabel(id)
                            }",
                        onRemove = {
                            onFiltersChange(
                                filters.copy(
                                    clothingTypeIds =
                                        filters
                                            .clothingTypeIds -
                                                id
                                )
                            )
                        }
                    )
                }
            }

        filters.colours
            .sorted()
            .forEach { id ->

                item(
                    key = "colour-$id"
                ) {
                    ActiveControlChip(
                        label =
                            "Colour: ${
                                wardrobeLabel(id)
                            }",
                        onRemove = {
                            onFiltersChange(
                                filters.copy(
                                    colours =
                                        filters.colours -
                                                id
                                )
                            )
                        }
                    )
                }
            }

        filters.patterns
            .sorted()
            .forEach { id ->

                item(
                    key = "pattern-$id"
                ) {
                    ActiveControlChip(
                        label =
                            "Pattern: ${
                                wardrobeLabel(id)
                            }",
                        onRemove = {
                            onFiltersChange(
                                filters.copy(
                                    patterns =
                                        filters.patterns -
                                                id
                                )
                            )
                        }
                    )
                }
            }

        filters.materials
            .sorted()
            .forEach { id ->

                item(
                    key = "material-$id"
                ) {
                    ActiveControlChip(
                        label =
                            "Material: ${
                                wardrobeLabel(id)
                            }",
                        onRemove = {
                            onFiltersChange(
                                filters.copy(
                                    materials =
                                        filters.materials -
                                                id
                                )
                            )
                        }
                    )
                }
            }

        filters.styles
            .sorted()
            .forEach { id ->

                item(
                    key = "style-$id"
                ) {
                    ActiveControlChip(
                        label =
                            "Style: ${
                                wardrobeLabel(id)
                            }",
                        onRemove = {
                            onFiltersChange(
                                filters.copy(
                                    styles =
                                        filters.styles -
                                                id
                                )
                            )
                        }
                    )
                }
            }

        filters.occasions
            .sorted()
            .forEach { id ->

                item(
                    key = "occasion-$id"
                ) {
                    ActiveControlChip(
                        label =
                            "Occasion: ${
                                wardrobeLabel(id)
                            }",
                        onRemove = {
                            onFiltersChange(
                                filters.copy(
                                    occasions =
                                        filters.occasions -
                                                id
                                )
                            )
                        }
                    )
                }
            }

        filters.seasons
            .sorted()
            .forEach { id ->

                item(
                    key = "season-$id"
                ) {
                    ActiveControlChip(
                        label =
                            "Season: ${
                                wardrobeLabel(id)
                            }",
                        onRemove = {
                            onFiltersChange(
                                filters.copy(
                                    seasons =
                                        filters.seasons -
                                                id
                                )
                            )
                        }
                    )
                }
            }

        filters.formalities
            .sorted()
            .forEach { id ->

                item(
                    key = "formality-$id"
                ) {
                    ActiveControlChip(
                        label =
                            "Formality: ${
                                wardrobeLabel(id)
                            }",
                        onRemove = {
                            onFiltersChange(
                                filters.copy(
                                    formalities =
                                        filters.formalities -
                                                id
                                )
                            )
                        }
                    )
                }
            }

        if (
            sortOrder !=
            WardrobeSortOrder.AUTO
        ) {
            item(
                key = "sort"
            ) {
                ActiveControlChip(
                    label =
                        "Sort: ${sortOrder.label}",
                    onRemove =
                        onSortReset
                )
            }
        }

        item(
            key = "reset"
        ) {
            TextButton(
                onClick = onResetAll
            ) {
                Text("Reset view")
            }
        }
    }
}

@Composable
private fun ActiveControlChip(
    label: String,
    onRemove: () -> Unit
) {
    FilterChip(
        selected = true,
        onClick = onRemove,
        label = {
            Text("$label ×")
        }
    )
}