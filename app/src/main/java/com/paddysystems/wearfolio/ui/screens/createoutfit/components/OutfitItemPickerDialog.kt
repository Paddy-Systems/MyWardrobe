package com.paddysystems.wearfolio.ui.screens.createoutfit.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paddysystems.wearfolio.data.model.WardrobeItem
import com.paddysystems.wearfolio.data.model.defaultClothingTypes
import com.paddysystems.wearfolio.search.WardrobeSearchEngine

@Composable
fun OutfitItemPickerDialog(
    title: String,
    items: List<WardrobeItem>,
    onSelect:
        (WardrobeItem?) -> Unit,
    onDismiss: () -> Unit
) {
    var query by remember {
        mutableStateOf("")
    }

    val visibleItems =
        if (query.isBlank()) {
            items
        } else {
            WardrobeSearchEngine
                .search(
                    items = items,
                    query = query
                )
                .map {
                    it.item
                }
        }

    AlertDialog(
        onDismissRequest =
            onDismiss,

        title = {
            Text(title, style = MaterialTheme.typography.headlineMedium)
        },

        text = {
            Column {
                OutlinedTextField(
                    value = query,

                    onValueChange = {
                        query = it
                    },

                    placeholder = {
                        Text(
                            "Search wardrobe"
                        )
                    },

                    singleLine = true,

                    modifier =
                        Modifier.fillMaxWidth()
                )

                LazyVerticalGrid(
                    columns =
                        GridCells.Fixed(3),

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .heightIn(
                                max = 420.dp
                            )
                            .padding(
                                top = 12.dp
                            )
                ) {
                    items(
                        items =
                            visibleItems,

                        key = {
                            it.id
                        }
                    ) { item ->

                        val typeName =
                            defaultClothingTypes
                                .firstOrNull {
                                    it.id ==
                                            item
                                                .clothingTypeId
                                }
                                ?.name
                                ?: item
                                    .clothingTypeId

                        Surface(
                            modifier =
                                Modifier
                                    .padding(
                                        4.dp
                                    )
                                    .clickable {
                                        onSelect(
                                            item
                                        )
                                    },

                            shape =
                                RoundedCornerShape(
                                    12.dp
                                ),

                            color =
                                MaterialTheme
                                    .colorScheme
                                    .surface
                        ) {
                            Column(
                                modifier =
                                    Modifier.padding(
                                        6.dp
                                    )
                            ) {
                                OutfitGarmentImage(
                                    item = item,

                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .aspectRatio(
                                                1f
                                            )
                                )

                                Text(
                                    text =
                                        typeName,

                                    style =
                                        MaterialTheme
                                            .typography
                                            .labelSmall
                                )
                            }
                        }
                    }
                }

                if (
                    visibleItems.isEmpty()
                ) {
                    Text(
                        text =
                            "No matching items",

                        modifier =
                            Modifier.padding(
                                top = 16.dp
                            )
                    )
                }
            }
        },

        confirmButton = {
            TextButton(
                onClick = {
                    onSelect(null)
                }
            ) {
                Text("Clear slot")
            }
        },

        dismissButton = {
            TextButton(
                onClick =
                    onDismiss
            ) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(28.dp),
        containerColor = MaterialTheme.colorScheme.background
    )
}
