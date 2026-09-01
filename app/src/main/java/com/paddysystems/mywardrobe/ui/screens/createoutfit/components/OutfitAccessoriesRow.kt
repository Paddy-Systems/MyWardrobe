package com.paddysystems.mywardrobe.ui.screens.createoutfit.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paddysystems.mywardrobe.data.model.OutfitSlotSelection
import com.paddysystems.mywardrobe.data.model.WardrobeItem
import com.paddysystems.mywardrobe.ui.screens.createoutfit.cycleOutfitItem

@Composable
fun OutfitAccessoriesRow(
    slots: List<OutfitSlotSelection>,
    accessoryItems:
    List<WardrobeItem>,
    onSlotChange:
        (
        Int,
        OutfitSlotSelection
    ) -> Unit,
    onSearch: (Int) -> Unit,
    onAdd: () -> Unit,
    onRemove: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = "Accessories",

            style =
                MaterialTheme
                    .typography
                    .titleMedium
        )

        Spacer(
            modifier =
                Modifier.height(6.dp)
        )

        LazyRow(
            horizontalArrangement =
                Arrangement.spacedBy(
                    10.dp
                )
        ) {
            itemsIndexed(
                slots
            ) {
                    index,
                    slot ->

                val item =
                    accessoryItems
                        .firstOrNull {
                            it.id ==
                                    slot.itemId
                        }

                AccessorySlotCard(
                    item = item,

                    slot = slot,

                    hasItems =
                        accessoryItems
                            .isNotEmpty(),

                    onPrevious = {
                        onSlotChange(
                            index,

                            slot.copy(
                                itemId =
                                    cycleOutfitItem(
                                        currentItemId =
                                            slot.itemId,

                                        items =
                                            accessoryItems,

                                        direction =
                                            -1
                                    )
                            )
                        )
                    },

                    onNext = {
                        onSlotChange(
                            index,

                            slot.copy(
                                itemId =
                                    cycleOutfitItem(
                                        currentItemId =
                                            slot.itemId,

                                        items =
                                            accessoryItems,

                                        direction =
                                            1
                                    )
                            )
                        )
                    },

                    onToggleLock = {
                        onSlotChange(
                            index,

                            slot.copy(
                                isLocked =
                                    !slot.isLocked
                            )
                        )
                    },

                    onSearch = {
                        onSearch(index)
                    },

                    onRemove = {
                        onRemove(index)
                    }
                )
            }

            item {
                FilledTonalButton(
                    modifier =
                        Modifier.height(
                            170.dp
                        ),

                    onClick =
                        onAdd
                ) {
                    Text(
                        text = "+",

                        fontSize =
                            28.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun AccessorySlotCard(
    item: WardrobeItem?,
    slot: OutfitSlotSelection,
    hasItems: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onToggleLock: () -> Unit,
    onSearch: () -> Unit,
    onRemove: () -> Unit
) {
    Surface(
        modifier = Modifier
            .width(170.dp)
            .height(170.dp),

        shape =
            RoundedCornerShape(
                18.dp
            ),

        color =
            MaterialTheme
                .colorScheme
                .surfaceVariant
                .copy(
                    alpha = 0.35f
                )
    ) {
        Box(
            modifier =
                Modifier.fillMaxSize()
        ) {
            if (item != null) {
                OutfitGarmentImage(
                    item = item,

                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            start = 34.dp,
                            end = 34.dp,
                            top = 30.dp,
                            bottom = 42.dp
                        )
                )
            } else {
                Text(
                    text = "Empty",

                    modifier =
                        Modifier.align(
                            Alignment.Center
                        )
                )
            }

            FilledTonalIconButton(
                modifier = Modifier
                    .align(
                        Alignment.TopEnd
                    )
                    .padding(4.dp)
                    .size(30.dp),

                onClick =
                    onRemove
            ) {
                Text("×")
            }

            Row(
                modifier = Modifier
                    .align(
                        Alignment.BottomCenter
                    )
                    .padding(
                        bottom = 5.dp
                    ),

                horizontalArrangement =
                    Arrangement.spacedBy(
                        2.dp
                    )
            ) {
                FilledTonalIconButton(
                    modifier =
                        Modifier.size(32.dp),

                    enabled =
                        hasItems,

                    onClick =
                        onPrevious
                ) {
                    Text("‹")
                }

                FilledTonalIconButton(
                    modifier =
                        Modifier.size(32.dp),

                    enabled =
                        item != null,

                    onClick =
                        onToggleLock
                ) {
                    Text(
                        if (slot.isLocked) {
                            "🔒"
                        } else {
                            "🔓"
                        },

                        fontSize = 12.sp
                    )
                }

                FilledTonalIconButton(
                    modifier =
                        Modifier.size(32.dp),

                    enabled =
                        hasItems,

                    onClick =
                        onSearch
                ) {
                    Text(
                        "🔍",
                        fontSize = 12.sp
                    )
                }

                FilledTonalIconButton(
                    modifier =
                        Modifier.size(32.dp),

                    enabled =
                        hasItems,

                    onClick =
                        onNext
                ) {
                    Text("›")
                }
            }
        }
    }
}