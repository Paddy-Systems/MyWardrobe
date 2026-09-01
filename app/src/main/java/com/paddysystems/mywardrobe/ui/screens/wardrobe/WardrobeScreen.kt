package com.paddysystems.mywardrobe.ui.screens.wardrobe

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.paddysystems.mywardrobe.data.model.WardrobeItem
import com.paddysystems.mywardrobe.search.WardrobeSortOrder
import com.paddysystems.mywardrobe.ui.components.DeleteConfirmationDialog
import com.paddysystems.mywardrobe.ui.components.SelectionActions
import com.paddysystems.mywardrobe.ui.components.WardrobeActiveControls
import com.paddysystems.mywardrobe.ui.components.WardrobeFilterDialog
import com.paddysystems.mywardrobe.ui.components.WardrobeGrid
import com.paddysystems.mywardrobe.ui.components.WardrobeHeader
import com.paddysystems.mywardrobe.ui.components.WardrobeSortDialog
import com.paddysystems.mywardrobe.ui.components.WardrobeToolbar

@Composable
fun WardrobeScreen(modifier: Modifier = Modifier, onItemClick: (WardrobeItem) -> Unit = {}) {
    val state = rememberWardrobeScreenState(LocalContext.current.applicationContext)
    val visibleItems = state.visibleItems

    Column(modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(horizontal = 20.dp, vertical = 22.dp)) {
        WardrobeHeader(visibleItems.size, state.items.size, state.selectedItems.size)
        SelectionActions(state.selectedItems.size, state::cancelSelection) { state.showDeleteConfirmation = true }
        Spacer(Modifier.height(12.dp))
        WardrobeToolbar(
            state.searchQuery,
            state::updateSearch,
            state::clearSearch,
            state.filters.activeCount,
            state.sortOrder != WardrobeSortOrder.AUTO,
            { state.showFilterDialog = true },
            { state.showSortDialog = true }
        )
        Spacer(Modifier.height(14.dp))
        if (state.hasActiveControls) {
            WardrobeActiveControls(state.filters, state.sortOrder, state.searchQuery.isNotBlank(), state::applyFilters, state::resetSort, state::resetAll)
            Spacer(Modifier.height(8.dp))
        }
        Spacer(Modifier.height(8.dp))
        WardrobeGrid(
            visibleItems,
            state.selectedItems,
            onItemClick = { if (state.selectedItems.isEmpty()) onItemClick(it) else state.toggleSelection(it) },
            onItemLongClick = state::toggleSelection,
            modifier = Modifier.weight(1f),
            emptyMessage = if (state.items.isEmpty()) "No items yet" else "No matching items"
        )
    }

    if (state.showFilterDialog) WardrobeFilterDialog(state.filters, state::applyFilters) { state.showFilterDialog = false }
    if (state.showSortDialog) WardrobeSortDialog(state.sortOrder, state::selectSort) { state.showSortDialog = false }
    if (state.showDeleteConfirmation) DeleteConfirmationDialog(state.selectedItems.size, state::deleteSelected) { state.showDeleteConfirmation = false }
}
