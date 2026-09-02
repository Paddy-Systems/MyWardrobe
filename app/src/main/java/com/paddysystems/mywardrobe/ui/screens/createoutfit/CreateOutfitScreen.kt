package com.paddysystems.mywardrobe.ui.screens.createoutfit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.paddysystems.mywardrobe.data.model.Outfit
import com.paddysystems.mywardrobe.data.model.OutfitLayer
import com.paddysystems.mywardrobe.data.model.OutfitLayerMode
import com.paddysystems.mywardrobe.data.model.OutfitPlacement
import com.paddysystems.mywardrobe.data.model.OutfitSlotSelection
import com.paddysystems.mywardrobe.data.model.WardrobeItem
import com.paddysystems.mywardrobe.data.model.outfitPlacement
import com.paddysystems.mywardrobe.data.storage.loadOutfit
import com.paddysystems.mywardrobe.data.storage.loadWardrobeItems
import com.paddysystems.mywardrobe.data.storage.saveOutfit
import com.paddysystems.mywardrobe.ui.LocalActiveProfile
import com.paddysystems.mywardrobe.ui.components.EditorialPageHeader
import com.paddysystems.mywardrobe.ui.components.EditorialPrimaryButton
import com.paddysystems.mywardrobe.ui.components.EditorialSecondaryButton
import com.paddysystems.mywardrobe.ui.screens.createoutfit.components.OutfitAccessoriesRow
import com.paddysystems.mywardrobe.ui.screens.createoutfit.components.OutfitItemPickerDialog
import com.paddysystems.mywardrobe.ui.screens.createoutfit.components.OutfitLayerEditor
import com.paddysystems.mywardrobe.ui.screens.createoutfit.components.OutfitStandaloneSlot
import com.paddysystems.mywardrobe.ui.screens.createoutfit.components.SaveOutfitDialog
import com.paddysystems.mywardrobe.ui.screens.createoutfit.components.StackedOutfitPreview
import java.util.UUID
import kotlin.math.absoluteValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun CreateOutfitScreen(
    outfitId: String? = null,
    onBack: (() -> Unit)? = null,
    onSaved: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val profileId = LocalActiveProfile.current.id
    val scope = rememberCoroutineScope()

    val editingOutfit = remember(outfitId, profileId) {
        outfitId?.let {
            loadOutfit(
                context = context,
                profileId = profileId,
                outfitId = it
            )
        }
    }

    if (outfitId != null && editingOutfit == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(top = 22.dp)
        ) {
            EditorialPageHeader(
                eyebrow = "Edit saved fit",
                title = "Fit unavailable",
                subtitle = "This saved fit could not be loaded.",
                navigationIcon = if (onBack != null) {
                    Icons.AutoMirrored.Outlined.ArrowBack
                } else {
                    null
                },
                onNavigate = onBack,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }
        return
    }

    val isEditing = editingOutfit != null

    var showSaveDialog by remember {
        mutableStateOf(false)
    }
    var isSaving by remember {
        mutableStateOf(false)
    }
    var saveError by remember {
        mutableStateOf<String?>(null)
    }

    val wardrobeItems = remember(profileId) {
        loadWardrobeItems(context, profileId)
    }

    val topItems = remember(wardrobeItems) {
        wardrobeItems.filter {
            it.outfitPlacement() == OutfitPlacement.TOP
        }
    }
    val bottomItems = remember(wardrobeItems) {
        wardrobeItems.filter {
            it.outfitPlacement() == OutfitPlacement.BOTTOM
        }
    }
    val fullLengthItems = remember(wardrobeItems) {
        wardrobeItems.filter {
            it.outfitPlacement() == OutfitPlacement.FULL_LENGTH
        }
    }
    val shoeItems = remember(wardrobeItems) {
        wardrobeItems.filter {
            it.outfitPlacement() == OutfitPlacement.SHOES
        }
    }
    val bagItems = remember(wardrobeItems) {
        wardrobeItems.filter {
            it.outfitPlacement() == OutfitPlacement.BAG
        }
    }
    val accessoryItems = remember(wardrobeItems) {
        wardrobeItems.filter {
            it.outfitPlacement() == OutfitPlacement.ACCESSORY
        }
    }

    val editableOutfit = remember(editingOutfit, wardrobeItems) {
        editingOutfit?.let {
            sanitiseOutfitForEditing(
                outfit = it,
                wardrobeItems = wardrobeItems
            )
        }
    }

    var shoes by remember(editableOutfit?.id) {
        mutableStateOf(
            editableOutfit?.shoes
                ?: OutfitSlotSelection()
        )
    }

    var bag by remember(editableOutfit?.id) {
        mutableStateOf(
            editableOutfit?.bag
                ?: OutfitSlotSelection()
        )
    }

    var accessories by remember(editableOutfit?.id) {
        mutableStateOf(
            editableOutfit?.accessories
                ?: emptyList()
        )
    }

    var layers by remember(editableOutfit?.id) {
        mutableStateOf(
            editableOutfit
                ?.layers
                ?.takeIf { it.isNotEmpty() }
                ?: listOf(
                    createOutfitLayer(
                        topItems = topItems,
                        bottomItems = bottomItems,
                        fullLengthItems = fullLengthItems,
                        preselectItems = !isEditing
                    )
                )
        )
    }

    val pagerState = rememberPagerState(
        pageCount = { layers.size }
    )

    var requestedPage by remember {
        mutableStateOf<Int?>(null)
    }

    LaunchedEffect(layers.size, requestedPage) {
        val page = requestedPage
            ?: return@LaunchedEffect

        if (layers.isNotEmpty()) {
            pagerState.animateScrollToPage(
                page.coerceIn(
                    0,
                    layers.lastIndex
                )
            )
        }

        requestedPage = null
    }

    var showStackedView by remember {
        mutableStateOf(false)
    }

    var pickerTarget by remember {
        mutableStateOf<OutfitPickerTarget?>(null)
    }

    val suggestedName = remember(
        layers,
        shoes,
        bag,
        accessories,
        wardrobeItems
    ) {
        suggestOutfitName(
            layers = layers,
            shoes = shoes,
            bag = bag,
            accessories = accessories,
            wardrobeItems = wardrobeItems
        )
    }

    val saveDialogName = if (isEditing) {
        editableOutfit?.name
            ?: suggestedName
    } else {
        suggestedName
    }

    val hasAnySelection =
        layers.any { layer ->
            when (layer.mode) {
                OutfitLayerMode.SEPARATES ->
                    layer.top.itemId != null ||
                        layer.bottom.itemId != null

                OutfitLayerMode.FULL_LENGTH ->
                    layer.fullLength.itemId != null
            }
        } ||
            shoes.itemId != null ||
            bag.itemId != null ||
            accessories.any {
                it.itemId != null
            }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(vertical = 22.dp)
    ) {
        EditorialPageHeader(
            eyebrow = if (isEditing) {
                "Edit saved fit"
            } else {
                "Fit studio"
            },
            title = if (isEditing) {
                editableOutfit?.name
                    ?: "Edit fit"
            } else {
                "Build a fit"
            },
            subtitle = if (isEditing) {
                "Adjust the layers and finishing pieces, then save your changes."
            } else {
                "Mix, layer and lock the pieces you love."
            },
            navigationIcon = if (isEditing && onBack != null) {
                Icons.AutoMirrored.Outlined.ArrowBack
            } else {
                null
            },
            onNavigate = if (isEditing) {
                onBack
            } else {
                null
            },
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(Modifier.height(12.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(18.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Row(
                modifier = Modifier.padding(6.dp)
            ) {
                if (!showStackedView) {
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = {}
                    ) {
                        Text("Layers")
                    }
                } else {
                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            showStackedView = false
                        }
                    ) {
                        Text("Layers")
                    }
                }

                Spacer(Modifier.width(8.dp))

                if (showStackedView) {
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = {}
                    ) {
                        Text("Stacked")
                    }
                } else {
                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            showStackedView = true
                        }
                    ) {
                        Text("Stacked")
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        if (showStackedView) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text(
                    "Layer 1 is the base layer. Higher layers are drawn on top."
                )

                Spacer(Modifier.height(8.dp))

                StackedOutfitPreview(
                    layers = layers,
                    wardrobeItems = wardrobeItems,
                    onLayerClick = { layerIndex ->
                        requestedPage = layerIndex
                        showStackedView = false
                    }
                )
            }
        } else {
            HorizontalPager(
                state = pagerState,
                contentPadding = PaddingValues(horizontal = 36.dp),
                pageSpacing = 12.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(650.dp)
            ) { page ->
                val pageOffset =
                    (
                        (pagerState.currentPage - page) +
                            pagerState.currentPageOffsetFraction
                        )
                        .absoluteValue
                        .coerceIn(0f, 1f)

                val layer = layers[page]

                OutfitLayerEditor(
                    layer = layer,
                    layerNumber = page + 1,
                    layerCount = layers.size,
                    topItems = topItems,
                    bottomItems = bottomItems,
                    fullLengthItems = fullLengthItems,
                    onLayerChange = { updatedLayer ->
                        layers = layers
                            .toMutableList()
                            .also {
                                it[page] = updatedLayer
                            }
                    },
                    onSearch = { placement ->
                        pickerTarget =
                            OutfitPickerTarget.Layer(
                                layerId = layer.id,
                                placement = placement
                            )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer {
                            scaleY =
                                1f -
                                    (pageOffset * 0.05f)

                            alpha =
                                1f -
                                    (pageOffset * 0.22f)
                        }
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                EditorialSecondaryButton(
                    text = "Remove layer",
                    modifier = Modifier.weight(1f),
                    enabled = layers.size > 1,
                    onClick = removeLayer@{
                        if (layers.size <= 1) {
                            return@removeLayer
                        }

                        val index = pagerState.currentPage
                            .coerceIn(
                                0,
                                layers.lastIndex
                            )

                        val updated = layers
                            .toMutableList()
                            .also {
                                it.removeAt(index)
                            }

                        layers = updated
                        requestedPage =
                            index.coerceAtMost(
                                updated.lastIndex
                            )
                    }
                )

                Spacer(Modifier.width(8.dp))

                EditorialPrimaryButton(
                    text = "Add layer",
                    modifier = Modifier.weight(1f),
                    onClick = {
                        val newPage = layers.size

                        layers =
                            layers +
                                createOutfitLayer(
                                    topItems = topItems,
                                    bottomItems = bottomItems,
                                    fullLengthItems = fullLengthItems,
                                    preselectItems = false
                                )

                        requestedPage = newPage
                    }
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        Column(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            val selectedShoes =
                shoeItems.firstOrNull {
                    it.id == shoes.itemId
                }

            OutfitStandaloneSlot(
                label = "Shoes",
                item = selectedShoes,
                isLocked = shoes.isLocked,
                hasItems = shoeItems.isNotEmpty(),
                onPrevious = {
                    shoes = shoes.copy(
                        itemId = cycleOutfitItem(
                            currentItemId = shoes.itemId,
                            items = shoeItems,
                            direction = -1
                        )
                    )
                },
                onNext = {
                    shoes = shoes.copy(
                        itemId = cycleOutfitItem(
                            currentItemId = shoes.itemId,
                            items = shoeItems,
                            direction = 1
                        )
                    )
                },
                onToggleLock = {
                    shoes = shoes.copy(
                        isLocked = !shoes.isLocked
                    )
                },
                onSearch = {
                    pickerTarget =
                        OutfitPickerTarget.Shoes
                }
            )

            Spacer(Modifier.height(20.dp))

            val selectedBag =
                bagItems.firstOrNull {
                    it.id == bag.itemId
                }

            OutfitStandaloneSlot(
                label = "Bag",
                item = selectedBag,
                isLocked = bag.isLocked,
                hasItems = bagItems.isNotEmpty(),
                onPrevious = {
                    bag = bag.copy(
                        itemId = cycleOutfitItem(
                            currentItemId = bag.itemId,
                            items = bagItems,
                            direction = -1
                        )
                    )
                },
                onNext = {
                    bag = bag.copy(
                        itemId = cycleOutfitItem(
                            currentItemId = bag.itemId,
                            items = bagItems,
                            direction = 1
                        )
                    )
                },
                onToggleLock = {
                    bag = bag.copy(
                        isLocked = !bag.isLocked
                    )
                },
                onSearch = {
                    pickerTarget =
                        OutfitPickerTarget.Bag
                }
            )

            Spacer(Modifier.height(20.dp))

            OutfitAccessoriesRow(
                slots = accessories,
                accessoryItems = accessoryItems,
                onSlotChange = {
                        index,
                        updatedSlot ->
                    accessories =
                        accessories
                            .toMutableList()
                            .also {
                                it[index] = updatedSlot
                            }
                },
                onSearch = { index ->
                    pickerTarget =
                        OutfitPickerTarget.Accessory(
                            index
                        )
                },
                onAdd = {
                    accessories =
                        accessories +
                            OutfitSlotSelection()
                },
                onRemove = { index ->
                    accessories =
                        accessories
                            .toMutableList()
                            .also {
                                it.removeAt(index)
                            }
                }
            )

            Spacer(Modifier.height(24.dp))

            val canShuffleWholeOutfit =
                layers.any { layer ->
                    when (layer.mode) {
                        OutfitLayerMode.SEPARATES ->
                            (
                                !layer.top.isLocked &&
                                    topItems.isNotEmpty()
                                ) ||
                                (
                                    !layer.bottom.isLocked &&
                                        bottomItems.isNotEmpty()
                                    )

                        OutfitLayerMode.FULL_LENGTH ->
                            !layer.fullLength.isLocked &&
                                fullLengthItems.isNotEmpty()
                    }
                } ||
                    (
                        !shoes.isLocked &&
                            shoeItems.isNotEmpty()
                        ) ||
                    (
                        !bag.isLocked &&
                            bagItems.isNotEmpty()
                        ) ||
                    accessories.any {
                        !it.isLocked &&
                            accessoryItems.isNotEmpty()
                    }

            EditorialSecondaryButton(
                text = "Shuffle the fit",
                icon = Icons.Outlined.AutoAwesome,
                modifier = Modifier.fillMaxWidth(),
                enabled = canShuffleWholeOutfit,
                onClick = {
                    layers =
                        layers.map { layer ->
                            shuffleOutfitLayer(
                                layer = layer,
                                topItems = topItems,
                                bottomItems = bottomItems,
                                fullLengthItems = fullLengthItems
                            )
                        }

                    shoes =
                        shuffleOutfitSlot(
                            slot = shoes,
                            items = shoeItems
                        )

                    bag =
                        shuffleOutfitSlot(
                            slot = bag,
                            items = bagItems
                        )

                    accessories =
                        accessories.map {
                            shuffleOutfitSlot(
                                slot = it,
                                items = accessoryItems
                            )
                        }
                }
            )

            Spacer(Modifier.height(12.dp))

            EditorialPrimaryButton(
                text = when {
                    isSaving ->
                        "Saving…"

                    isEditing ->
                        "Save changes"

                    else ->
                        "Save fit"
                },
                icon = Icons.Outlined.Save,
                modifier = Modifier.fillMaxWidth(),
                enabled =
                    hasAnySelection &&
                        !isSaving,
                onClick = {
                    saveError = null
                    showSaveDialog = true
                }
            )

            saveError?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        Spacer(Modifier.height(24.dp))
    }

    if (showSaveDialog) {
        SaveOutfitDialog(
            suggestedName = saveDialogName,
            title = if (isEditing) {
                "Save your changes"
            } else {
                "Name this fit"
            },
            confirmText = if (isEditing) {
                "Update"
            } else {
                "Save"
            },
            onDismiss = {
                showSaveDialog = false
            },
            onSave = { name ->
                showSaveDialog = false
                isSaving = true
                saveError = null

                val outfit =
                    Outfit(
                        id =
                            editableOutfit
                                ?.id
                                ?: UUID
                                    .randomUUID()
                                    .toString(),
                        name = name,
                        layers = layers,
                        shoes = shoes,
                        bag = bag,
                        accessories = accessories,
                        previewPath =
                            editableOutfit
                                ?.previewPath,
                        createdAt =
                            editableOutfit
                                ?.createdAt
                                ?: System
                                    .currentTimeMillis()
                    )

                scope.launch {
                    val saved =
                        withContext(
                            Dispatchers.IO
                        ) {
                            saveOutfit(
                                context =
                                    context
                                        .applicationContext,
                                profileId = profileId,
                                outfit = outfit
                            )
                        }

                    isSaving = false

                    if (saved) {
                        onSaved(outfit.id)
                    } else {
                        saveError =
                            if (isEditing) {
                                "Could not update fit"
                            } else {
                                "Could not save fit"
                            }
                    }
                }
            }
        )
    }

    pickerTarget?.let { target ->
        val eligibleItems =
            when (target) {
                is OutfitPickerTarget.Layer ->
                    when (target.placement) {
                        OutfitPlacement.TOP ->
                            topItems

                        OutfitPlacement.BOTTOM ->
                            bottomItems

                        OutfitPlacement.FULL_LENGTH ->
                            fullLengthItems

                        else ->
                            emptyList()
                    }

                OutfitPickerTarget.Shoes ->
                    shoeItems

                OutfitPickerTarget.Bag ->
                    bagItems

                is OutfitPickerTarget.Accessory ->
                    accessoryItems
            }

        val title =
            when (target) {
                is OutfitPickerTarget.Layer ->
                    when (target.placement) {
                        OutfitPlacement.TOP ->
                            "Choose top"

                        OutfitPlacement.BOTTOM ->
                            "Choose bottom"

                        OutfitPlacement.FULL_LENGTH ->
                            "Choose full-length item"

                        else ->
                            "Choose item"
                    }

                OutfitPickerTarget.Shoes ->
                    "Choose shoes"

                OutfitPickerTarget.Bag ->
                    "Choose bag"

                is OutfitPickerTarget.Accessory ->
                    "Choose accessory"
            }

        OutfitItemPickerDialog(
            title = title,
            items = eligibleItems,
            onSelect = { selectedItem ->
                when (target) {
                    is OutfitPickerTarget.Layer -> {
                        val layerIndex =
                            layers.indexOfFirst {
                                it.id ==
                                    target.layerId
                            }

                        if (layerIndex != -1) {
                            val updated =
                                updateLayerSelection(
                                    layer =
                                        layers[
                                            layerIndex
                                        ],
                                    placement =
                                        target
                                            .placement,
                                    item =
                                        selectedItem
                                )

                            layers =
                                layers
                                    .toMutableList()
                                    .also {
                                        it[layerIndex] =
                                            updated
                                    }
                        }
                    }

                    OutfitPickerTarget.Shoes -> {
                        shoes =
                            shoes.copy(
                                itemId =
                                    selectedItem
                                        ?.id
                            )
                    }

                    OutfitPickerTarget.Bag -> {
                        bag =
                            bag.copy(
                                itemId =
                                    selectedItem
                                        ?.id
                            )
                    }

                    is OutfitPickerTarget.Accessory -> {
                        if (
                            target.index
                            in accessories.indices
                        ) {
                            accessories =
                                accessories
                                    .toMutableList()
                                    .also {
                                        it[target.index] =
                                            it[target.index]
                                                .copy(
                                                    itemId =
                                                        selectedItem
                                                            ?.id
                                                )
                                    }
                        }
                    }
                }

                pickerTarget = null
            },
            onDismiss = {
                pickerTarget = null
            }
        )
    }
}

private sealed interface OutfitPickerTarget {
    data class Layer(
        val layerId: String,
        val placement: OutfitPlacement
    ) : OutfitPickerTarget

    data object Shoes : OutfitPickerTarget

    data object Bag : OutfitPickerTarget

    data class Accessory(
        val index: Int
    ) : OutfitPickerTarget
}

private fun createOutfitLayer(
    topItems: List<WardrobeItem>,
    bottomItems: List<WardrobeItem>,
    fullLengthItems: List<WardrobeItem>,
    preselectItems: Boolean
): OutfitLayer {
    return OutfitLayer(
        id = UUID.randomUUID().toString(),
        mode = OutfitLayerMode.SEPARATES,
        top = OutfitSlotSelection(
            itemId =
                if (preselectItems) {
                    topItems
                        .firstOrNull()
                        ?.id
                } else {
                    null
                }
        ),
        bottom = OutfitSlotSelection(
            itemId =
                if (preselectItems) {
                    bottomItems
                        .firstOrNull()
                        ?.id
                } else {
                    null
                }
        ),
        fullLength = OutfitSlotSelection(
            itemId =
                if (preselectItems) {
                    fullLengthItems
                        .firstOrNull()
                        ?.id
                } else {
                    null
                }
        )
    )
}

private fun updateLayerSelection(
    layer: OutfitLayer,
    placement: OutfitPlacement,
    item: WardrobeItem?
): OutfitLayer {
    return when (placement) {
        OutfitPlacement.TOP ->
            layer.copy(
                top =
                    layer.top.copy(
                        itemId = item?.id
                    )
            )

        OutfitPlacement.BOTTOM ->
            layer.copy(
                bottom =
                    layer.bottom.copy(
                        itemId = item?.id
                    )
            )

        OutfitPlacement.FULL_LENGTH ->
            layer.copy(
                fullLength =
                    layer.fullLength.copy(
                        itemId = item?.id
                    )
            )

        else ->
            layer
    }
}
