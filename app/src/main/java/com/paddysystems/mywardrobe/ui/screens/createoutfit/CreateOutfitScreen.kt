package com.paddysystems.mywardrobe.ui.screens.createoutfit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.paddysystems.mywardrobe.data.model.OutfitLayer
import com.paddysystems.mywardrobe.data.model.OutfitPlacement
import com.paddysystems.mywardrobe.data.model.OutfitSlotSelection
import com.paddysystems.mywardrobe.data.model.WardrobeItem
import com.paddysystems.mywardrobe.data.model.outfitPlacement
import com.paddysystems.mywardrobe.data.storage.loadWardrobeItems
import com.paddysystems.mywardrobe.ui.screens.createoutfit.components.OutfitItemPickerDialog
import com.paddysystems.mywardrobe.ui.screens.createoutfit.components.OutfitLayerEditor
import java.util.UUID

@Composable
fun CreateOutfitScreen() {
    val context =
        LocalContext.current

    val wardrobeItems =
        remember {
            loadWardrobeItems(
                context
            )
        }

    val topItems =
        remember(
            wardrobeItems
        ) {
            wardrobeItems.filter {
                it.outfitPlacement() ==
                        OutfitPlacement.TOP
            }
        }

    val bottomItems =
        remember(
            wardrobeItems
        ) {
            wardrobeItems.filter {
                it.outfitPlacement() ==
                        OutfitPlacement.BOTTOM
            }
        }

    val fullLengthItems =
        remember(
            wardrobeItems
        ) {
            wardrobeItems.filter {
                it.outfitPlacement() ==
                        OutfitPlacement
                            .FULL_LENGTH
            }
        }

    var layer by remember {
        mutableStateOf(
            OutfitLayer(
                id =
                    UUID.randomUUID()
                        .toString(),

                top =
                    OutfitSlotSelection(
                        itemId =
                            topItems
                                .firstOrNull()
                                ?.id
                    ),

                bottom =
                    OutfitSlotSelection(
                        itemId =
                            bottomItems
                                .firstOrNull()
                                ?.id
                    ),

                fullLength =
                    OutfitSlotSelection(
                        itemId =
                            fullLengthItems
                                .firstOrNull()
                                ?.id
                    )
            )
        )
    }

    var pickerPlacement
            by remember {
                mutableStateOf<
                        OutfitPlacement?
                        >(null)
            }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(
                rememberScrollState()
            )
            .padding(16.dp)
    ) {
        Text("Create Outfit")

        Spacer(
            modifier =
                Modifier.height(16.dp)
        )

        OutfitLayerEditor(
            layer = layer,

            topItems =
                topItems,

            bottomItems =
                bottomItems,

            fullLengthItems =
                fullLengthItems,

            onLayerChange = {
                layer = it
            },

            onSearch = {
                pickerPlacement = it
            }
        )

        Spacer(
            modifier =
                Modifier.height(24.dp)
        )
    }

    pickerPlacement
        ?.let { placement ->

            val eligibleItems =
                when (placement) {
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
                    when (placement) {
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

                    layer =
                        updateLayerSelection(
                            layer =
                                layer,

                            placement =
                                placement,

                            item =
                                selectedItem
                        )

                    pickerPlacement =
                        null
                },

                onDismiss = {
                    pickerPlacement =
                        null
                }
            )
        }
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