package com.paddysystems.mywardrobe.ui.screens.createoutfit.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paddysystems.mywardrobe.data.model.WardrobeItem

@Composable
fun OutfitSlotOverlay(
    item: WardrobeItem?,
    isLocked: Boolean,
    hasItems: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onToggleLock: () -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
    ) {
        if (item != null) {
            OutfitGarmentImage(
                item = item,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        horizontal = 58.dp,
                        vertical = 4.dp
                    )
            )
        } else {
            Text(
                text =
                    if (hasItems) {
                        "Choose an item"
                    } else {
                        "No items available"
                    },
                modifier =
                    Modifier.align(
                        Alignment.Center
                    ),
                color =
                    MaterialTheme
                        .colorScheme
                        .onSurfaceVariant
            )
        }

        FilledTonalIconButton(
            modifier = Modifier
                .align(
                    Alignment.CenterStart
                )
                .size(44.dp),
            enabled = hasItems,
            onClick = onPrevious
        ) {
            Text(
                text = "‹",
                fontSize = 30.sp
            )
        }

        FilledTonalIconButton(
            modifier = Modifier
                .align(
                    Alignment.CenterEnd
                )
                .size(44.dp),
            enabled = hasItems,
            onClick = onNext
        ) {
            Text(
                text = "›",
                fontSize = 30.sp
            )
        }

        Row(
            modifier = Modifier
                .align(
                    Alignment.CenterEnd
                )
                .padding(
                    end = 52.dp
                ),
            horizontalArrangement =
                Arrangement.spacedBy(
                    4.dp
                )
        ) {
            FilledTonalIconButton(
                modifier =
                    Modifier.size(38.dp),
                enabled =
                    item != null,
                onClick =
                    onToggleLock
            ) {
                Text(
                    text =
                        if (isLocked) {
                            "🔒"
                        } else {
                            "🔓"
                        },
                    fontSize = 16.sp
                )
            }

            FilledTonalIconButton(
                modifier =
                    Modifier.size(38.dp),
                enabled =
                    hasItems,
                onClick =
                    onSearch
            ) {
                Text(
                    text = "🔍",
                    fontSize = 16.sp
                )
            }
        }
    }
}