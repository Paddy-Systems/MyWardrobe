package com.paddysystems.mywardrobe.ui.screens.outfits

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.paddysystems.mywardrobe.data.storage.loadOutfits
import com.paddysystems.mywardrobe.data.storage.loadWardrobeItems
import com.paddysystems.mywardrobe.ui.screens.outfits.components.OutfitThumbnail

@Composable
fun OutfitsScreen(
    refreshKey: Int = 0
) {
    val context =
        LocalContext.current

    val outfits =
        remember(
            refreshKey
        ) {
            loadOutfits(
                context
            )
        }

    val wardrobeItems =
        remember {
            loadWardrobeItems(
                context
            )
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = 16.dp
            )
    ) {

        Text(
            text = "Saved Outfits",

            style =
                MaterialTheme
                    .typography
                    .headlineMedium,

            modifier =
                Modifier.padding(
                    horizontal = 16.dp
                )
        )

        if (outfits.isEmpty()) {

            Text(
                text =
                    "No saved outfits yet",

                modifier =
                    Modifier.padding(
                        16.dp
                    )
            )

        } else {

            LazyVerticalGrid(
                columns =
                    GridCells.Fixed(2),

                contentPadding =
                    PaddingValues(
                        12.dp
                    )
            ) {
                items(
                    items =
                        outfits,

                    key = {
                        it.id
                    }
                ) { outfit ->

                    Surface(
                        modifier =
                            Modifier.padding(
                                6.dp
                            ),

                        shape =
                            RoundedCornerShape(
                                20.dp
                            ),

                        tonalElevation =
                            2.dp
                    ) {
                        Column {

                            OutfitThumbnail(
                                outfit =
                                    outfit,

                                wardrobeItems =
                                    wardrobeItems,

                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(
                                        0.78f
                                    )
                            )

                            Text(
                                text =
                                    outfit.name,

                                style =
                                    MaterialTheme
                                        .typography
                                        .titleMedium,

                                modifier =
                                    Modifier.padding(
                                        12.dp
                                    )
                            )
                        }
                    }
                }
            }
        }
    }
}