package com.paddysystems.mywardrobe.ui.screens.wardrobe

import android.content.Context
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.paddysystems.mywardrobe.data.model.WardrobeItem
import com.paddysystems.mywardrobe.data.storage.deleteWardrobeItem
import com.paddysystems.mywardrobe.data.storage.loadWardrobeItems
import com.paddysystems.mywardrobe.search.WardrobeFilterEngine
import com.paddysystems.mywardrobe.search.WardrobeFilters
import com.paddysystems.mywardrobe.search.WardrobeSearchEngine
import com.paddysystems.mywardrobe.search.WardrobeSortEngine
import com.paddysystems.mywardrobe.search.WardrobeSortOrder

@Stable
class WardrobeScreenState(private val context: Context) {
    var searchQuery by mutableStateOf("")
        private set
    var filters by mutableStateOf(WardrobeFilters())
        private set
    var sortOrder by mutableStateOf(WardrobeSortOrder.AUTO)
        private set
    var showFilterDialog by mutableStateOf(false)
    var showSortDialog by mutableStateOf(false)
    var showDeleteConfirmation by mutableStateOf(false)
    val selectedItems = mutableStateListOf<WardrobeItem>()
    val items = mutableStateListOf<WardrobeItem>().apply { addAll(loadWardrobeItems(context)) }

    val visibleItems: List<WardrobeItem>
        get() = WardrobeSortEngine.sort(
            WardrobeSearchEngine.search(WardrobeFilterEngine.filter(items, filters), searchQuery),
            sortOrder,
            searchQuery.isNotBlank()
        )

    val hasActiveControls: Boolean
        get() = searchQuery.isNotBlank() || !filters.isEmpty || sortOrder != WardrobeSortOrder.AUTO

    fun updateSearch(query: String) { searchQuery = query; selectedItems.clear() }
    fun clearSearch() = updateSearch("")
    fun applyFilters(value: WardrobeFilters) { filters = value; selectedItems.clear(); showFilterDialog = false }
    fun selectSort(value: WardrobeSortOrder) { sortOrder = value; selectedItems.clear(); showSortDialog = false }
    fun resetSort() { sortOrder = WardrobeSortOrder.AUTO; selectedItems.clear() }
    fun resetAll() { searchQuery = ""; filters = WardrobeFilters(); sortOrder = WardrobeSortOrder.AUTO; selectedItems.clear() }
    fun toggleSelection(item: WardrobeItem) { if (!selectedItems.remove(item)) selectedItems.add(item) }
    fun cancelSelection() = selectedItems.clear()
    fun deleteSelected() {
        selectedItems.toList().forEach { if (deleteWardrobeItem(context, it)) items.remove(it) }
        selectedItems.clear()
        showDeleteConfirmation = false
    }
}
