package com.paddysystems.mywardrobe.ui.screens.createoutfit

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
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
import com.paddysystems.mywardrobe.data.model.OutfitLayer
import com.paddysystems.mywardrobe.data.model.OutfitLayerMode
import com.paddysystems.mywardrobe.data.model.OutfitPlacement
import com.paddysystems.mywardrobe.data.model.OutfitSlotSelection
import com.paddysystems.mywardrobe.data.model.WardrobeItem
import com.paddysystems.mywardrobe.data.model.outfitPlacement
import com.paddysystems.mywardrobe.data.storage.loadWardrobeItems
import com.paddysystems.mywardrobe.ui.screens.createoutfit.components.OutfitItemPickerDialog
import com.paddysystems.mywardrobe.ui.screens.createoutfit.components.OutfitLayerEditor
import com.paddysystems.mywardrobe.ui.screens.createoutfit.components.StackedOutfitPreview
import java.util.UUID
import kotlin.math.absoluteValue
import com.paddysystems.mywardrobe.ui.screens.createoutfit.components.OutfitStandaloneSlot
import com.paddysystems.mywardrobe.ui.screens.createoutfit.components.OutfitAccessoriesRow
import androidx.compose.runtime.rememberCoroutineScope
import com.paddysystems.mywardrobe.data.model.Outfit
import com.paddysystems.mywardrobe.data.storage.saveOutfit
import com.paddysystems.mywardrobe.ui.screens.createoutfit.components.SaveOutfitDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Save
import com.paddysystems.mywardrobe.ui.components.EditorialPageHeader
import com.paddysystems.mywardrobe.ui.components.EditorialPrimaryButton
import com.paddysystems.mywardrobe.ui.components.EditorialSecondaryButton

@Composable
fun CreateOutfitScreen(
    onSaved: () -> Unit = {}
) {

    val context =
        LocalContext.current

    val scope =
        rememberCoroutineScope()

    var showSaveDialog by remember {
        mutableStateOf(false)
    }

    var isSaving by remember {
        mutableStateOf(false)
    }

    var saveError by remember {
        mutableStateOf<String?>(null)
    }


    val wardrobeItems =
        remember {
            loadWardrobeItems(
                context
            )
        }

    val topItems =
        remember(wardrobeItems) {
            wardrobeItems.filter {
                it.outfitPlacement() ==
                        OutfitPlacement.TOP
            }
        }

    val bottomItems =
        remember(wardrobeItems) {
            wardrobeItems.filter {
                it.outfitPlacement() ==
                        OutfitPlacement.BOTTOM
            }
        }

    val fullLengthItems =
        remember(wardrobeItems) {
            wardrobeItems.filter {
                it.outfitPlacement() ==
                        OutfitPlacement.FULL_LENGTH
            }
        }

    val shoeItems =
        remember(wardrobeItems) {
            wardrobeItems.filter {
                it.outfitPlacement() ==
                        OutfitPlacement.SHOES
            }
        }

    val bagItems =
        remember(wardrobeItems) {
            wardrobeItems.filter {
                it.outfitPlacement() ==
                        OutfitPlacement.BAG
            }
        }

    val accessoryItems =
        remember(wardrobeItems) {
            wardrobeItems.filter {
                it.outfitPlacement() ==
                        OutfitPlacement.ACCESSORY
            }
        }

    var shoes by remember {
        mutableStateOf(
            OutfitSlotSelection()
        )
    }

    var bag by remember {
        mutableStateOf(
            OutfitSlotSelection()
        )
    }

    var accessories by remember {
        mutableStateOf<
                List<OutfitSlotSelection>
                >(
            emptyList()
        )
    }

    var layers by remember {
        mutableStateOf(
            listOf(
                createOutfitLayer(
                    topItems =
                        topItems,

                    bottomItems =
                        bottomItems,

                    fullLengthItems =
                        fullLengthItems,

                    preselectItems =
                        true
                )
            )
        )
    }

    val pagerState =
        rememberPagerState(
            pageCount = {
                layers.size
            }
        )

    var requestedPage by remember {
        mutableStateOf<Int?>(null)
    }

    val suggestedName =
        remember(
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
                accessories =
                    accessories,
                wardrobeItems =
                    wardrobeItems
            )
        }

    val hasAnySelection =
        layers.any { layer ->

            when (layer.mode) {

                OutfitLayerMode.SEPARATES ->
                    layer.top.itemId != null ||
                            layer.bottom.itemId != null

                OutfitLayerMode.FULL_LENGTH ->
                    layer.fullLength
                        .itemId != null
            }

        } ||
                shoes.itemId != null ||
                bag.itemId != null ||
                accessories.any {
                    it.itemId != null
                }

    /*
     * Run after the layer list has
     * recomposed, so Pager knows the
     * new page count.
     */
    LaunchedEffect(
        layers.size,
        requestedPage
    ) {
        val page =
            requestedPage
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
        mutableStateOf<
                OutfitPickerTarget?
                >(null)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(
                rememberScrollState()
            )
            .padding(
                vertical = 22.dp
            )
    ) {
        EditorialPageHeader(
            eyebrow = "Outfit studio",
            title = "Build a look",
            subtitle = "Mix, layer and lock the pieces you love.",
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(
            modifier =
                Modifier.height(12.dp)
        )

        /*
         * Layers / Stacked toggle
         */
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(18.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
        Row(modifier = Modifier.padding(6.dp)) {

            if (!showStackedView) {
                Button(
                    modifier =
                        Modifier.weight(1f),

                    onClick = {}
                ) {
                    Text("Layers")
                }
            } else {
                OutlinedButton(
                    modifier =
                        Modifier.weight(1f),

                    onClick = {
                        showStackedView =
                            false
                    }
                ) {
                    Text("Layers")
                }
            }

            Spacer(
                modifier =
                    Modifier.width(8.dp)
            )

            if (showStackedView) {
                Button(
                    modifier =
                        Modifier.weight(1f),

                    onClick = {}
                ) {
                    Text("Stacked")
                }
            } else {
                OutlinedButton(
                    modifier =
                        Modifier.weight(1f),

                    onClick = {
                        showStackedView =
                            true
                    }
                ) {
                    Text("Stacked")
                }
            }
        }
        }

        Spacer(
            modifier =
                Modifier.height(12.dp)
        )

        if (showStackedView) {

            Column(
                modifier =
                    Modifier.padding(
                        horizontal = 16.dp
                    )
            ) {

                Text(
                    "Layer 1 is the base layer. " +
                            "Higher layers are drawn on top."
                )

                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )

                StackedOutfitPreview(
                    layers =
                        layers,

                    wardrobeItems =
                        wardrobeItems,

                    onLayerClick = {
                            layerIndex ->

                        requestedPage =
                            layerIndex

                        showStackedView =
                            false
                    }
                )
            }

        } else {

            /*
             * Playing-card layer deck.
             *
             * Side content padding exposes
             * part of the previous/next
             * layer card.
             */
            HorizontalPager(
                state =
                    pagerState,

                contentPadding =
                    PaddingValues(
                        horizontal = 36.dp
                    ),

                pageSpacing =
                    12.dp,

                modifier = Modifier
                    .fillMaxWidth()
                    .height(
                        650.dp
                    )
            ) { page ->

                val pageOffset =
                    (
                            (
                                    pagerState
                                        .currentPage -
                                            page
                                    ) +
                                    pagerState
                                        .currentPageOffsetFraction
                            )
                        .absoluteValue
                        .coerceIn(
                            0f,
                            1f
                        )

                val layer =
                    layers[page]

                OutfitLayerEditor(
                    layer =
                        layer,

                    layerNumber =
                        page + 1,

                    layerCount =
                        layers.size,

                    topItems =
                        topItems,

                    bottomItems =
                        bottomItems,

                    fullLengthItems =
                        fullLengthItems,

                    onLayerChange = {
                            updatedLayer ->

                        layers =
                            layers
                                .toMutableList()
                                .also {
                                    it[page] =
                                        updatedLayer
                                }
                    },

                    onSearch = {
                            placement ->

                        pickerTarget =
                            OutfitPickerTarget.Layer(
                                layerId =
                                    layer.id,

                                placement =
                                    placement
                            )
                    },

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .graphicsLayer {

                                /*
                                 * The neighbouring cards
                                 * sit slightly "behind"
                                 * the active one.
                                 */
                                scaleY =
                                    1f -
                                            (
                                                    pageOffset *
                                                            0.05f
                                                    )

                                alpha =
                                    1f -
                                            (
                                                    pageOffset *
                                                            0.22f
                                                    )
                            }
                )
            }

            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 16.dp
                    )
            ) {

                EditorialSecondaryButton(
                    text = "Remove layer",
                    modifier =
                        Modifier.weight(1f),

                    enabled =
                        layers.size > 1,

                    onClick = removeLayer@ {

                        if (
                            layers.size <= 1
                        ) {
                            return@removeLayer
                        }

                        val index =
                            pagerState
                                .currentPage
                                .coerceIn(
                                    0,
                                    layers.lastIndex
                                )

                        val updated =
                            layers
                                .toMutableList()
                                .also {
                                    it.removeAt(
                                        index
                                    )
                                }

                        layers = updated

                        requestedPage =
                            index.coerceAtMost(
                                updated.lastIndex
                            )
                    }
                )

                Spacer(
                    modifier =
                        Modifier.width(8.dp)
                )

                EditorialPrimaryButton(
                    text = "Add layer",
                    modifier =
                        Modifier.weight(1f),

                    onClick = {

                        val newPage =
                            layers.size

                        layers =
                            layers +
                                    createOutfitLayer(
                                        topItems =
                                            topItems,

                                        bottomItems =
                                            bottomItems,

                                        fullLengthItems =
                                            fullLengthItems,

                                        preselectItems =
                                            false
                                    )

                        requestedPage =
                            newPage
                    }
                )
            }
        }

        Spacer(
            modifier =
                Modifier.height(24.dp)
        )

        Column(
            modifier =
                Modifier.padding(
                    horizontal = 16.dp
                )
        ) {
            val selectedShoes =
                shoeItems.firstOrNull {
                    it.id == shoes.itemId
                }

            OutfitStandaloneSlot(
                label = "Shoes",

                item =
                    selectedShoes,

                isLocked =
                    shoes.isLocked,

                hasItems =
                    shoeItems.isNotEmpty(),

                onPrevious = {
                    shoes =
                        shoes.copy(
                            itemId =
                                cycleOutfitItem(
                                    currentItemId =
                                        shoes.itemId,

                                    items =
                                        shoeItems,

                                    direction = -1
                                )
                        )
                },

                onNext = {
                    shoes =
                        shoes.copy(
                            itemId =
                                cycleOutfitItem(
                                    currentItemId =
                                        shoes.itemId,

                                    items =
                                        shoeItems,

                                    direction = 1
                                )
                        )
                },

                onToggleLock = {
                    shoes =
                        shoes.copy(
                            isLocked =
                                !shoes.isLocked
                        )
                },

                onSearch = {
                    pickerTarget =
                        OutfitPickerTarget.Shoes
                }
            )

            Spacer(
                modifier =
                    Modifier.height(20.dp)
            )

            val selectedBag =
                bagItems.firstOrNull {
                    it.id == bag.itemId
                }

            OutfitStandaloneSlot(
                label = "Bag",

                item =
                    selectedBag,

                isLocked =
                    bag.isLocked,

                hasItems =
                    bagItems.isNotEmpty(),

                onPrevious = {
                    bag =
                        bag.copy(
                            itemId =
                                cycleOutfitItem(
                                    currentItemId =
                                        bag.itemId,

                                    items =
                                        bagItems,

                                    direction = -1
                                )
                        )
                },

                onNext = {
                    bag =
                        bag.copy(
                            itemId =
                                cycleOutfitItem(
                                    currentItemId =
                                        bag.itemId,

                                    items =
                                        bagItems,

                                    direction = 1
                                )
                        )
                },

                onToggleLock = {
                    bag =
                        bag.copy(
                            isLocked =
                                !bag.isLocked
                        )
                },

                onSearch = {
                    pickerTarget =
                        OutfitPickerTarget.Bag
                }
            )
        }

        if (showSaveDialog) {

            SaveOutfitDialog(
                suggestedName =
                    suggestedName,

                onDismiss = {
                    showSaveDialog =
                        false
                },

                onSave = {
                        name ->

                    showSaveDialog =
                        false

                    isSaving =
                        true

                    saveError =
                        null

                    val outfit =
                        Outfit(
                            id =
                                UUID.randomUUID()
                                    .toString(),

                            name =
                                name,

                            layers =
                                layers,

                            shoes =
                                shoes,

                            bag =
                                bag,

                            accessories =
                                accessories,

                            createdAt =
                                System
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

                                    outfit =
                                        outfit
                                )
                            }

                        isSaving =
                            false

                        if (saved) {
                            onSaved()
                        } else {
                            saveError =
                                "Could not save outfit"
                        }
                    }
                }
            )
        }

        Spacer(
            modifier =
                Modifier.height(20.dp)
        )

        OutfitAccessoriesRow(
            slots =
                accessories,

            accessoryItems =
                accessoryItems,

            onSlotChange = {
                    index,
                    updatedSlot ->

                accessories =
                    accessories
                        .toMutableList()
                        .also {
                            it[index] =
                                updatedSlot
                        }
            },

            onSearch = {
                    index ->

                pickerTarget =
                    OutfitPickerTarget
                        .Accessory(
                            index
                        )
            },

            onAdd = {
                accessories =
                    accessories +
                            OutfitSlotSelection()
            },

            onRemove = {
                    index ->

                accessories =
                    accessories
                        .toMutableList()
                        .also {
                            it.removeAt(
                                index
                            )
                        }
            }
        )

        Spacer(
            modifier =
                Modifier.height(24.dp)
        )

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
                        !layer
                            .fullLength
                            .isLocked &&
                                fullLengthItems
                                    .isNotEmpty()
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
            text = "Shuffle the look",
            icon = Icons.Outlined.AutoAwesome,
            modifier =
                Modifier.fillMaxWidth(),

            enabled =
                canShuffleWholeOutfit,

            onClick = {

                layers =
                    layers.map { layer ->

                        shuffleOutfitLayer(
                            layer = layer,

                            topItems =
                                topItems,

                            bottomItems =
                                bottomItems,

                            fullLengthItems =
                                fullLengthItems
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
                            items =
                                accessoryItems
                        )
                    }
            }
        )

        Spacer(
            modifier =
                Modifier.height(12.dp)
        )

        EditorialPrimaryButton(
            text = if (isSaving) "Saving…" else "Save outfit",
            icon = Icons.Outlined.Save,
            modifier =
                Modifier.fillMaxWidth(),

            enabled =
                hasAnySelection &&
                        !isSaving,

            onClick = {
                saveError = null
                showSaveDialog = true
            }
        )

        saveError?.let {
            Text(
                text = it,

                modifier =
                    Modifier.padding(
                        top = 8.dp
                    )
            )
        }

        Spacer(
            modifier =
                Modifier.height(24.dp)
        )
    }

    pickerTarget
        ?.let { target ->

            val eligibleItems =
                when (target) {

                    is OutfitPickerTarget.Layer ->
                        when (
                            target.placement
                        ) {
                            OutfitPlacement.TOP ->
                                topItems

                            OutfitPlacement.BOTTOM ->
                                bottomItems

                            OutfitPlacement
                                .FULL_LENGTH ->
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
                        when (
                            target.placement
                        ) {
                            OutfitPlacement.TOP ->
                                "Choose top"

                            OutfitPlacement.BOTTOM ->
                                "Choose bottom"

                            OutfitPlacement
                                .FULL_LENGTH ->
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

                items =
                    eligibleItems,

                onSelect = {
                        selectedItem ->

                    when (target) {

                        is OutfitPickerTarget.Layer -> {

                            val layerIndex =
                                layers.indexOfFirst {
                                    it.id ==
                                            target.layerId
                                }

                            if (
                                layerIndex != -1
                            ) {
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
                                            it[
                                                target.index
                                            ] =
                                                it[
                                                    target.index
                                                ].copy(
                                                    itemId =
                                                        selectedItem
                                                            ?.id
                                                )
                                        }
                            }
                        }
                    }

                    pickerTarget =
                        null
                },

                onDismiss = {
                    pickerTarget =
                        null
                }
            )
        }
}

private sealed interface OutfitPickerTarget {

    data class Layer(
        val layerId: String,
        val placement:
        OutfitPlacement
    ) : OutfitPickerTarget

    data object Shoes :
        OutfitPickerTarget

    data object Bag :
        OutfitPickerTarget

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
        id =
            UUID.randomUUID()
                .toString(),

        mode =
            OutfitLayerMode.SEPARATES,

        top =
            OutfitSlotSelection(
                itemId =
                    if (preselectItems) {
                        topItems
                            .firstOrNull()
                            ?.id
                    } else {
                        null
                    }
            ),

        bottom =
            OutfitSlotSelection(
                itemId =
                    if (preselectItems) {
                        bottomItems
                            .firstOrNull()
                            ?.id
                    } else {
                        null
                    }
            ),

        fullLength =
            OutfitSlotSelection(
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
                        itemId =
                            item?.id
                    )
            )

        OutfitPlacement.BOTTOM ->
            layer.copy(
                bottom =
                    layer.bottom.copy(
                        itemId =
                            item?.id
                    )
            )

        OutfitPlacement
            .FULL_LENGTH ->
            layer.copy(
                fullLength =
                    layer.fullLength
                        .copy(
                            itemId =
                                item?.id
                        )
            )

        else ->
            layer
    }
}
