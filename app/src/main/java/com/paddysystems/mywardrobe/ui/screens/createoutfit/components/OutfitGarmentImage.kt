package com.paddysystems.mywardrobe.ui.screens.createoutfit.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import com.paddysystems.mywardrobe.data.model.WardrobeItem
import java.io.File

@Composable
fun OutfitGarmentImage(
    item: WardrobeItem,
    modifier: Modifier = Modifier
) {
    val cutoutFile =
        item.cutoutPath
            ?.let {
                File(it)
            }
            ?.takeIf {
                it.exists()
            }

    val imageFile =
        cutoutFile
            ?: File(
                item.imagePath
            )

    AsyncImage(
        model = imageFile,

        contentDescription =
            "Fit clothing item",

        contentScale =
            ContentScale.Fit,

        modifier =
            modifier
    )
}
