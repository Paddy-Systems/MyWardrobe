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
import com.paddysystems.mywardrobe.search.WardrobeFilterEngine
import com.paddysystems.mywardrobe.search.WardrobeFilters
import com.paddysystems.mywardrobe.search.WardrobeSortEngine
import com.paddysystems.mywardrobe.search.WardrobeSortOrder
import com.paddysystems.mywardrobe.ui.components.WardrobeFilterDialog
import com.paddysystems.mywardrobe.ui.components.WardrobeSortDialog
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
import com.paddysystems.mywardrobe.search.WardrobeSearchEngine

@Composable
fun WardrobeScreen(
    modifier: Modifier = Modifier,
    onAddItemClick: () -> Unit = {},
    onItemClick: (WardrobeItem) -> Unit = {}
) {
    val context = LocalContext.current

    var searchQuery by remember {
        mutableStateOf("")
    }

    var filters by remember {
        mutableStateOf(
            WardrobeFilters()
        )
    }

    var sortOrder by remember {
        mutableStateOf(
            WardrobeSortOrder.AUTO
        )
    }

    var showFilterDialog by remember {
        mutableStateOf(false)
    }

    var showSortDialog by remember {
        mutableStateOf(false)
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

    val filteredItems =
        WardrobeFilterEngine
            .filter(
                items = items,
                filters = filters
            )

    val searchResults =
        WardrobeSearchEngine
            .search(
                items = filteredItems,
                query = searchQuery
            )

    val visibleItems =
        WardrobeSortEngine
            .sort(
                results = searchResults,
                sortOrder = sortOrder,
                hasSearchQuery =
                    searchQuery.isNotBlank()
            )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        WardrobeHeader(
            itemCount = visibleItems.size,
            selectedCount =
                selectedItems.size
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

            onSearchQueryChange = { query ->
                searchQuery = query
                selectedItems.clear()
            },

            activeFilterCount =
                filters.activeCount,

            onFilterClick = {
                showFilterDialog = true
            },

            onSortClick = {
                showSortDialog = true
            }
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        WardrobeGrid(
            items = visibleItems,
            selectedItems = selectedItems,

            emptyMessage =
                if (
                    items.isEmpty()
                ) {
                    "No items yet"
                } else {
                    "No matching items"
                },

            modifier = Modifier.weight(1f),

            onItemClick = { item ->
                if (
                    selectedItems.isNotEmpty()
                ) {
                    if (
                        selectedItems.contains(
                            item
                        )
                    ) {
                        selectedItems.remove(item)
                    } else {
                        selectedItems.add(item)
                    }
                } else {
                    onItemClick(item)
                }
            },

            onItemLongClick = { item ->
                if (
                    selectedItems.contains(item)
                ) {
                    selectedItems.remove(item)
                } else {
                    selectedItems.add(item)
                }
            }
        )

        if (showFilterDialog) {
            WardrobeFilterDialog(
                filters = filters,

                onApply = {
                        updatedFilters ->

                    filters =
                        updatedFilters

                    selectedItems.clear()

                    showFilterDialog =
                        false
                },

                onDismiss = {
                    showFilterDialog =
                        false
                }
            )
        }

        if (showSortDialog) {
            WardrobeSortDialog(
                selectedOrder =
                    sortOrder,

                onSelect = {
                        order ->

                    sortOrder = order

                    selectedItems.clear()

                    showSortDialog =
                        false
                },

                onDismiss = {
                    showSortDialog =
                        false
                }
            )
        }

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