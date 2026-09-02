package com.paddysystems.wearfolio.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paddysystems.wearfolio.data.model.defaultClothingTypes
import com.paddysystems.wearfolio.data.model.wardrobeColours
import com.paddysystems.wearfolio.data.model.wardrobeFormalities
import com.paddysystems.wearfolio.data.model.wardrobeLabel
import com.paddysystems.wearfolio.data.model.wardrobeMaterials
import com.paddysystems.wearfolio.data.model.wardrobeOccasions
import com.paddysystems.wearfolio.data.model.wardrobePatterns
import com.paddysystems.wearfolio.data.model.wardrobeSeasons
import com.paddysystems.wearfolio.data.model.wardrobeStyles
import com.paddysystems.wearfolio.search.WardrobeFilters

@Composable
fun WardrobeFilterDialog(
    filters: WardrobeFilters,
    onApply: (WardrobeFilters) -> Unit,
    onDismiss: () -> Unit
) {
    var draftFilters by remember(filters) {
        mutableStateOf(filters)
    }

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text("Filter wardrobe")
        },

        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {

                if (!draftFilters.isEmpty) {
                    TextButton(
                        onClick = {
                            draftFilters =
                                WardrobeFilters()
                        }
                    ) {
                        Text("Clear all")
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .heightIn(
                            max = 520.dp
                        )
                ) {

                    item {
                        FilterSection(
                            title =
                                "Clothing type",

                            options =
                                defaultClothingTypes
                                    .map {
                                        it.id to
                                                it.name
                                    },

                            selected =
                                draftFilters
                                    .clothingTypeIds,

                            onToggle = { id ->
                                draftFilters =
                                    draftFilters.copy(
                                        clothingTypeIds =
                                            toggleValue(
                                                draftFilters
                                                    .clothingTypeIds,
                                                id
                                            )
                                    )
                            }
                        )
                    }

                    item {
                        FilterSection(
                            title = "Colour",

                            options =
                                wardrobeColours
                                    .map {
                                        it to
                                                wardrobeLabel(
                                                    it
                                                )
                                    },

                            selected =
                                draftFilters.colours,

                            onToggle = { id ->
                                draftFilters =
                                    draftFilters.copy(
                                        colours =
                                            toggleValue(
                                                draftFilters
                                                    .colours,
                                                id
                                            )
                                    )
                            }
                        )
                    }

                    item {
                        FilterSection(
                            title = "Pattern",
                            options =
                                optionsFor(
                                    wardrobePatterns
                                ),
                            selected =
                                draftFilters.patterns,
                            onToggle = { id ->
                                draftFilters =
                                    draftFilters.copy(
                                        patterns =
                                            toggleValue(
                                                draftFilters
                                                    .patterns,
                                                id
                                            )
                                    )
                            }
                        )
                    }

                    item {
                        FilterSection(
                            title = "Material",
                            options =
                                optionsFor(
                                    wardrobeMaterials
                                ),
                            selected =
                                draftFilters.materials,
                            onToggle = { id ->
                                draftFilters =
                                    draftFilters.copy(
                                        materials =
                                            toggleValue(
                                                draftFilters
                                                    .materials,
                                                id
                                            )
                                    )
                            }
                        )
                    }

                    item {
                        FilterSection(
                            title = "Style",
                            options =
                                optionsFor(
                                    wardrobeStyles
                                ),
                            selected =
                                draftFilters.styles,
                            onToggle = { id ->
                                draftFilters =
                                    draftFilters.copy(
                                        styles =
                                            toggleValue(
                                                draftFilters
                                                    .styles,
                                                id
                                            )
                                    )
                            }
                        )
                    }

                    item {
                        FilterSection(
                            title = "Occasion",
                            options =
                                optionsFor(
                                    wardrobeOccasions
                                ),
                            selected =
                                draftFilters.occasions,
                            onToggle = { id ->
                                draftFilters =
                                    draftFilters.copy(
                                        occasions =
                                            toggleValue(
                                                draftFilters
                                                    .occasions,
                                                id
                                            )
                                    )
                            }
                        )
                    }

                    item {
                        FilterSection(
                            title = "Season",
                            options =
                                optionsFor(
                                    wardrobeSeasons
                                ),
                            selected =
                                draftFilters.seasons,
                            onToggle = { id ->
                                draftFilters =
                                    draftFilters.copy(
                                        seasons =
                                            toggleValue(
                                                draftFilters
                                                    .seasons,
                                                id
                                            )
                                    )
                            }
                        )
                    }

                    item {
                        FilterSection(
                            title = "Formality",
                            options =
                                optionsFor(
                                    wardrobeFormalities
                                ),
                            selected =
                                draftFilters.formalities,
                            onToggle = { id ->
                                draftFilters =
                                    draftFilters.copy(
                                        formalities =
                                            toggleValue(
                                                draftFilters
                                                    .formalities,
                                                id
                                            )
                                    )
                            }
                        )
                    }
                }
            }
        },

        confirmButton = {
            TextButton(
                onClick = {
                    onApply(
                        draftFilters
                    )
                }
            ) {
                Text("Apply")
            }
        },

        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun FilterSection(
    title: String,
    options: List<Pair<String, String>>,
    selected: Set<String>,
    onToggle: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(title)

        LazyRow(
            horizontalArrangement =
                Arrangement.spacedBy(
                    8.dp
                ),

            contentPadding =
                PaddingValues(
                    vertical = 6.dp
                )
        ) {
            items(
                options,
                key = {
                    it.first
                }
            ) { option ->

                val id =
                    option.first

                val label =
                    option.second

                FilterChip(
                    selected =
                        id in selected,

                    onClick = {
                        onToggle(id)
                    },

                    label = {
                        Text(label)
                    }
                )
            }
        }
    }
}

private fun toggleValue(
    values: Set<String>,
    value: String
): Set<String> {

    return if (
        value in values
    ) {
        values - value
    } else {
        values + value
    }
}

private fun optionsFor(
    ids: List<String>
): List<Pair<String, String>> {

    return ids.map { id ->
        id to wardrobeLabel(id)
    }
}