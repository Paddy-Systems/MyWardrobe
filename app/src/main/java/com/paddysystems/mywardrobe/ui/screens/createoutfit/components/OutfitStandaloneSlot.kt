package com.paddysystems.mywardrobe.ui.screens.createoutfit.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paddysystems.mywardrobe.data.model.WardrobeItem

@Composable
fun OutfitStandaloneSlot(
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
    Column(
        modifier = modifier
    ) {
        Text(
            text = label,

            style =
                MaterialTheme
                    .typography
                    .titleMedium
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .padding(
                    top = 6.dp
                ),

            shape =
                RoundedCornerShape(
                    18.dp
                ),

            color =
                MaterialTheme
                    .colorScheme
                    .surface
        ) {
            OutfitSlotOverlay(
                item = item,
                isLocked = isLocked,
                hasItems = hasItems,
                onPrevious = onPrevious,
                onNext = onNext,
                onToggleLock =
                    onToggleLock,
                onSearch = onSearch,

                modifier =
                    Modifier.fillMaxWidth()
            )
        }
    }
}
