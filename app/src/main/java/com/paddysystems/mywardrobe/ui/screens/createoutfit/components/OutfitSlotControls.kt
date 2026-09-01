package com.paddysystems.mywardrobe.ui.screens.createoutfit.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.paddysystems.mywardrobe.data.model.WardrobeItem
import com.paddysystems.mywardrobe.data.model.defaultClothingTypes

@Composable
fun OutfitSlotControls(
    label: String,
    item: WardrobeItem?,
    isLocked: Boolean,
    hasItems: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onToggleLock: () -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    val itemName =
        item
            ?.let { wardrobeItem ->

                defaultClothingTypes
                    .firstOrNull {
                        it.id ==
                                wardrobeItem
                                    .clothingTypeId
                    }
                    ?.name

            }
            ?: "Empty"

    Row(
        modifier =
            modifier.fillMaxWidth(),

        verticalAlignment =
            Alignment.CenterVertically,

        horizontalArrangement =
            Arrangement.SpaceBetween
    ) {
        FilledTonalButton(
            enabled =
                hasItems,

            onClick =
                onPrevious
        ) {
            Text("←")
        }

        Column(
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {
            Text(label)

            Text(
                itemName
            )
        }

        TextButton(
            onClick =
                onToggleLock
        ) {
            Text(
                if (isLocked) {
                    "🔒"
                } else {
                    "🔓"
                }
            )
        }

        TextButton(
            onClick =
                onSearch
        ) {
            Text("Search")
        }

        FilledTonalButton(
            enabled =
                hasItems,

            onClick =
                onNext
        ) {
            Text("→")
        }
    }
}