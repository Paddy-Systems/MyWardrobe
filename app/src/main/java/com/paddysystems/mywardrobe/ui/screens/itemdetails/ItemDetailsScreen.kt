package com.paddysystems.mywardrobe.ui.screens.itemdetails

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.paddysystems.mywardrobe.data.model.defaultClothingTypes
import com.paddysystems.mywardrobe.data.storage.loadWardrobeItem
import java.io.File

@Composable
fun ItemDetailsScreen(
    itemId: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    val item = remember(itemId) {
        loadWardrobeItem(
            context = context,
            itemId = itemId
        )
    }

    if (item == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text("Item not found")

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Button(
                onClick = onBack
            ) {
                Text("Back")
            }
        }

        return
    }

    val clothingTypeName =
        defaultClothingTypes
            .firstOrNull {
                it.id == item.clothingTypeId
            }
            ?.name
            ?: item.clothingTypeId

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Item Details")

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        AsyncImage(
            model = File(item.imagePath),
            contentDescription = "Wardrobe item",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clip(
                    RoundedCornerShape(16.dp)
                )
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Text(
            "Type: $clothingTypeName"
        )

        Text(
            "Colours: ${
                item.colours.joinToString {
                        colour ->
                    colour.replaceFirstChar {
                        it.uppercase()
                    }
                }
            }"
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Button(
            onClick = onBack
        ) {
            Text("Back")
        }
    }
}