package com.paddysystems.mywardrobe
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paddysystems.mywardrobe.ui.theme.MyWardrobeTheme
import java.io.File
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.height

import com.paddysystems.mywardrobe.ui.components.WardrobeGrid
import com.paddysystems.mywardrobe.ui.components.WardrobeToolbar
import com.paddysystems.mywardrobe.ui.components.SelectionActions
import com.paddysystems.mywardrobe.ui.components.DeleteConfirmationDialog
import com.paddysystems.mywardrobe.ui.components.AddItemButton
import com.paddysystems.mywardrobe.ui.components.WardrobeHeader
import com.paddysystems.mywardrobe.data.model.WardrobeItem
import com.paddysystems.mywardrobe.data.storage.loadWardrobeItems
import com.paddysystems.mywardrobe.data.storage.deleteWardrobeItem

@Composable
fun WardrobeScreen(
    modifier: Modifier = Modifier,
    onAddItemClick: () -> Unit = {},
    onItemClick: (File) -> Unit = {}
) {
    val context = LocalContext.current

    var searchQuery by remember {
        mutableStateOf("")
    }

    val selectedItems = remember {
        mutableStateListOf<WardrobeItem>()
    }

    var showDeleteConfirmation by remember {
        mutableStateOf(false)
    }

    val items = remember {
        mutableStateListOf<WardrobeItem>().apply {
            addAll(
                loadWardrobeItems(context)
            )
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        WardrobeHeader(
            itemCount = items.size,
            selectedCount = selectedItems.size
        )
        SelectionActions(
            selectedCount = selectedItems.size,
            onCancel = {
                selectedItems.clear()
            },
            onDelete = {
                showDeleteConfirmation = true
            }
        )
        Spacer(
            modifier = Modifier.height(12.dp)
        )
        WardrobeToolbar(
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            onFilterClick = {
                //
            },
            onSortClick = {
                //
            }
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        WardrobeGrid(
            items = items,
            selectedItems = selectedItems,
            modifier = Modifier.weight(1f),
            onItemClick = { item ->
                if (selectedItems.isNotEmpty()) {
                    if (selectedItems.contains(item)) {
                        selectedItems.remove(item)
                    } else {
                        selectedItems.add(item)
                    }
                } else {
                    onItemClick(
                        File(item.imagePath)
                    )
                }
            },
            onItemLongClick = { item ->
                if (selectedItems.contains(item)) {
                    selectedItems.remove(item)
                } else {
                    selectedItems.add(item)
                }
            }
        )

        if (showDeleteConfirmation) {
            DeleteConfirmationDialog(
                selectedCount = selectedItems.size,
                onDismiss = {
                    showDeleteConfirmation = false
                },
                onConfirm = {
                    val itemsToDelete =
                        selectedItems.toList()

                    itemsToDelete.forEach { item ->
                        if (
                            deleteWardrobeItem(
                                context = context,
                                item = item
                            )
                        ) {
                            items.remove(item)
                        }
                    }

                    selectedItems.clear()
                    showDeleteConfirmation = false
                }
            )
        }

        AddItemButton(
            onClick = onAddItemClick
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WardrobePreview() {
    MyWardrobeTheme {
        WardrobeScreen()
    }
}