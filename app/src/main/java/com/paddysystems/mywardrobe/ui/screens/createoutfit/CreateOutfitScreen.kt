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

@Composable
fun CreateOutfitScreen() {

    val context =
        LocalContext.current

    val scope =
        rememberCoroutineScope()

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
            .verticalScroll(
                rememberScrollState()
            )
            .padding(
                vertical = 16.dp
            )
    ) {

        Text(
            text = "Create Outfit",

            modifier =
                Modifier.padding(
                    horizontal = 16.dp
                )
        )

        Spacer(
            modifier =
                Modifier.height(12.dp)
        )

        /*
         * Layers / Stacked toggle
         */
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp
                )
        ) {

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
                        horizontal = 52.dp
                    ),

                pageSpacing =
                    12.dp,

                modifier = Modifier
                    .fillMaxWidth()
                    .height(
                        680.dp
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
                            OutfitPickerTarget(
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

                OutlinedButton(
                    modifier =
                        Modifier.weight(1f),

                    enabled =
                        layers.size > 1,

                    onClick = {

                        if (
                            layers.size <= 1
                        ) {
                            return@OutlinedButton
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
                ) {
                    Text("Remove layer")
                }

                Spacer(
                    modifier =
                        Modifier.width(8.dp)
                )

                Button(
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
                ) {
                    Text("+ Add layer")
                }
            }
        }

        Spacer(
            modifier =
                Modifier.height(24.dp)
        )
    }

    pickerTarget
        ?.let { target ->

            val eligibleItems =
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

            OutfitItemPickerDialog(
                title =
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
                    },

                items =
                    eligibleItems,

                onSelect = {
                        selectedItem ->

                    val layerIndex =
                        layers.indexOfFirst {
                            it.id ==
                                    target.layerId
                        }

                    if (layerIndex != -1) {

                        val updatedLayer =
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
                                        updatedLayer
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

private data class OutfitPickerTarget(
    val layerId: String,
    val placement: OutfitPlacement
)

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