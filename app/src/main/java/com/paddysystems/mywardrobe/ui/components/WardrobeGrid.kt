package com.paddysystems.mywardrobe.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paddysystems.mywardrobe.data.model.WardrobeItem

@Composable
fun WardrobeGrid(
    items: List<WardrobeItem>,
    selectedItems: List<WardrobeItem>,
    onItemClick: (WardrobeItem) -> Unit,
    onItemLongClick: (WardrobeItem) -> Unit,
    modifier: Modifier = Modifier,
    emptyMessage: String =
        "No items yet"
) {
    if (items.isEmpty()) {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(emptyMessage)
        }

        return
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(
            items = items,
            key = { item ->
                item.id
            }
        ) { item ->
            WardrobeGridItem(
                item = item,
                isSelected = selectedItems.contains(item),
                onClick = {
                    onItemClick(item)
                },
                onLongClick = {
                    onItemLongClick(item)
                }
            )
        }
    }
}
