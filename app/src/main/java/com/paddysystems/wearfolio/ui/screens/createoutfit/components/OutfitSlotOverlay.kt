package com.paddysystems.wearfolio.ui.screens.createoutfit.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.LockOpen
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paddysystems.wearfolio.data.model.WardrobeItem

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
            Icon(Icons.Rounded.ChevronLeft, contentDescription = "Previous")
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
            Icon(Icons.Rounded.ChevronRight, contentDescription = "Next")
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
                Icon(if (isLocked) Icons.Outlined.Lock else Icons.Outlined.LockOpen, contentDescription = "Lock item")
            }

            FilledTonalIconButton(
                modifier =
                    Modifier.size(38.dp),
                enabled =
                    hasItems,
                onClick =
                    onSearch
            ) {
                Icon(Icons.Outlined.Search, contentDescription = "Choose item")
            }
        }
    }
}
