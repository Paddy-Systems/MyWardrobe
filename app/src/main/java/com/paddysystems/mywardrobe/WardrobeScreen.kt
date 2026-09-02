package com.paddysystems.mywardrobe

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paddysystems.mywardrobe.data.model.Profile
import com.paddysystems.mywardrobe.data.model.WardrobeItem
import com.paddysystems.mywardrobe.data.model.referencedItemIds
import com.paddysystems.mywardrobe.data.storage.WardrobeImportQueue
import com.paddysystems.mywardrobe.data.storage.deleteWardrobeItem
import com.paddysystems.mywardrobe.data.storage.loadOutfits
import com.paddysystems.mywardrobe.data.storage.loadWardrobeItems
import com.paddysystems.mywardrobe.search.WardrobeFilterEngine
import com.paddysystems.mywardrobe.search.WardrobeFilters
import com.paddysystems.mywardrobe.search.WardrobeSearchEngine
import com.paddysystems.mywardrobe.search.WardrobeSortEngine
import com.paddysystems.mywardrobe.search.WardrobeSortOrder
import com.paddysystems.mywardrobe.ui.components.DeleteConfirmationDialog
import com.paddysystems.mywardrobe.ui.components.SelectionActions
import com.paddysystems.mywardrobe.ui.components.WardrobeActiveControls
import com.paddysystems.mywardrobe.ui.components.WardrobeFilterDialog
import com.paddysystems.mywardrobe.ui.components.WardrobeGrid
import com.paddysystems.mywardrobe.ui.components.WardrobeHeader
import com.paddysystems.mywardrobe.ui.components.WardrobeImportStatus
import com.paddysystems.mywardrobe.ui.components.WardrobeSortDialog
import com.paddysystems.mywardrobe.ui.components.WardrobeToolbar
import com.paddysystems.mywardrobe.ui.LocalActiveProfile
import com.paddysystems.mywardrobe.ui.theme.MyWardrobeTheme
import com.paddysystems.mywardrobe.worker.WardrobeBatchImporter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun WardrobeScreen(
    modifier: Modifier = Modifier,
    refreshKey: Int = 0,
    onWardrobeChanged: () -> Unit = {},
    onItemClick: (WardrobeItem) -> Unit = {},
    onManageWardrobes: () -> Unit = {}
) {
    val context = LocalContext.current
    val profile = LocalActiveProfile.current
    val profileId = profile.id

    var searchQuery by remember { mutableStateOf("") }
    var filters by remember { mutableStateOf(WardrobeFilters()) }
    var sortOrder by remember { mutableStateOf(WardrobeSortOrder.AUTO) }
    var showFilterDialog by remember { mutableStateOf(false) }
    var showSortDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    val selectedItems = remember { mutableStateListOf<WardrobeItem>() }

    val items = remember(refreshKey, profileId) {
        mutableStateListOf<WardrobeItem>().apply {
            addAll(loadWardrobeItems(context, profileId))
        }
    }

    val importFlow = remember(context, profileId) {
        WardrobeImportQueue.observe(context.applicationContext, profileId)
    }
    val pendingImports by importFlow.collectAsState()
    val pendingImportIds = pendingImports.map { it.id }

    LaunchedEffect(refreshKey, profileId, pendingImportIds) {
        val reloadedItems = withContext(Dispatchers.IO) {
            loadWardrobeItems(context.applicationContext, profileId)
        }

        items.clear()
        items.addAll(reloadedItems)
    }

    val filteredItems = WardrobeFilterEngine.filter(
        items = items,
        filters = filters
    )

    val searchResults = WardrobeSearchEngine.search(
        items = filteredItems,
        query = searchQuery
    )

    val visibleItems = WardrobeSortEngine.sort(
        results = searchResults,
        sortOrder = sortOrder,
        hasSearchQuery = searchQuery.isNotBlank()
    )

    val hasActiveControls =
        searchQuery.isNotBlank() ||
            !filters.isEmpty ||
            sortOrder != WardrobeSortOrder.AUTO

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp, vertical = 22.dp)
    ) {
        WardrobeHeader(
            profileName = profile.name,
            itemCount = visibleItems.size + pendingImports.size,
            totalItemCount = items.size + pendingImports.size,
            selectedCount = selectedItems.size,
            onManageWardrobes = onManageWardrobes
        )

        SelectionActions(
            selectedCount = selectedItems.size,
            onCancel = { selectedItems.clear() },
            onDelete = { showDeleteConfirmation = true }
        )

        if (pendingImports.isNotEmpty()) {
            Spacer(Modifier.height(12.dp))
            WardrobeImportStatus(imports = pendingImports)
        }

        Spacer(Modifier.height(12.dp))

        WardrobeToolbar(
            searchQuery = searchQuery,
            onSearchQueryChange = { query ->
                searchQuery = query
                selectedItems.clear()
            },
            onClearSearch = {
                searchQuery = ""
                selectedItems.clear()
            },
            activeFilterCount = filters.activeCount,
            sortIsActive = sortOrder != WardrobeSortOrder.AUTO,
            onFilterClick = { showFilterDialog = true },
            onSortClick = { showSortDialog = true }
        )

        Spacer(Modifier.height(14.dp))

        if (hasActiveControls) {
            WardrobeActiveControls(
                filters = filters,
                sortOrder = sortOrder,
                hasSearchQuery = searchQuery.isNotBlank(),
                onFiltersChange = { updatedFilters ->
                    filters = updatedFilters
                    selectedItems.clear()
                },
                onSortReset = {
                    sortOrder = WardrobeSortOrder.AUTO
                    selectedItems.clear()
                },
                onResetAll = {
                    searchQuery = ""
                    filters = WardrobeFilters()
                    sortOrder = WardrobeSortOrder.AUTO
                    selectedItems.clear()
                }
            )
            Spacer(Modifier.height(8.dp))
        }

        Spacer(Modifier.height(8.dp))

        WardrobeGrid(
            items = visibleItems,
            pendingImports = pendingImports,
            selectedItems = selectedItems,
            emptyMessage = if (items.isEmpty() && pendingImports.isEmpty()) {
                "No items yet"
            } else {
                "No matching items"
            },
            modifier = Modifier.weight(1f),
            onImportRetry = { pendingImport ->
                if (
                    WardrobeImportQueue.markQueued(
                        context = context.applicationContext,
                        profileId = profileId,
                        importId = pendingImport.id
                    ) != null
                ) {
                    WardrobeBatchImporter.retry(
                        context = context.applicationContext,
                        profileId = profileId,
                        importId = pendingImport.id
                    )
                }
            },
            onImportRemove = { pendingImport ->
                WardrobeImportQueue.discardImport(
                    context = context.applicationContext,
                    profileId = profileId,
                    importId = pendingImport.id
                )
            },
            onItemClick = { item ->
                if (selectedItems.isNotEmpty()) {
                    if (selectedItems.contains(item)) {
                        selectedItems.remove(item)
                    } else {
                        selectedItems.add(item)
                    }
                } else {
                    onItemClick(item)
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

        if (showFilterDialog) {
            WardrobeFilterDialog(
                filters = filters,
                onApply = { updatedFilters ->
                    filters = updatedFilters
                    selectedItems.clear()
                    showFilterDialog = false
                },
                onDismiss = { showFilterDialog = false }
            )
        }

        if (showSortDialog) {
            WardrobeSortDialog(
                selectedOrder = sortOrder,
                onSelect = { order ->
                    sortOrder = order
                    selectedItems.clear()
                    showSortDialog = false
                },
                onDismiss = { showSortDialog = false }
            )
        }

        if (showDeleteConfirmation) {
            val selectedIds = selectedItems.map { it.id }.toSet()
            val affectedOutfitCount = loadOutfits(context, profileId)
                .count { outfit ->
                    outfit.referencedItemIds().any { it in selectedIds }
                }

            DeleteConfirmationDialog(
                selectedCount = selectedItems.size,
                affectedOutfitCount = affectedOutfitCount,
                onDismiss = { showDeleteConfirmation = false },
                onConfirm = {
                    val itemsToDelete = selectedItems.toList()
                    var changed = false

                    itemsToDelete.forEach { item ->
                        if (
                            deleteWardrobeItem(
                                context = context,
                                profileId = profileId,
                                item = item
                            )
                        ) {
                            items.remove(item)
                            changed = true
                        }
                    }

                    selectedItems.clear()
                    showDeleteConfirmation = false

                    if (changed) {
                        onWardrobeChanged()
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WardrobePreview() {
    MyWardrobeTheme {
        CompositionLocalProvider(
            LocalActiveProfile provides Profile(id = "preview", name = "Preview")
        ) {
            WardrobeScreen()
        }
    }
}
