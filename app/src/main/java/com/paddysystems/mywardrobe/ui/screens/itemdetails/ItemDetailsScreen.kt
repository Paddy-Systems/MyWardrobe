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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.paddysystems.mywardrobe.data.model.wardrobeLabel
import com.paddysystems.mywardrobe.ui.screens.itemdetails.components.SemanticMetadataSection
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface

@Composable
fun ItemDetailsScreen(
    itemId: String,
    onEdit: () -> Unit,
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

    val cutoutFile =
        item.cutoutPath
            ?.let {
                File(it)
            }
            ?.takeIf {
                it.exists()
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
            .verticalScroll(
                rememberScrollState()
            )
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

        if (cutoutFile != null) {

            Spacer(
                modifier =
                    Modifier.height(16.dp)
            )

            Text(
                "Transparent cut-out"
            )

            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )

            Surface(
                modifier =
                    Modifier.fillMaxWidth(),

                color =
                    MaterialTheme
                        .colorScheme
                        .surfaceVariant,

                shape =
                    RoundedCornerShape(
                        16.dp
                    )
            ) {

                AsyncImage(
                    model =
                        cutoutFile,

                    contentDescription =
                        "Transparent clothing cut-out",

                    contentScale =
                        ContentScale.Fit,

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(
                                300.dp
                            )
                            .padding(
                                12.dp
                            )
                )
            }
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Text(
            "Type: $clothingTypeName"
        )

        Text(
            text =
                "Colours: ${
                    item.colours
                        .joinToString {
                            wardrobeLabel(it)
                        }
                }"
        )

        Spacer(
            modifier =
                Modifier.height(24.dp)
        )

        SemanticMetadataSection(
            metadata =
                item.metadata
        )

        Spacer(
            modifier =
                Modifier.height(24.dp)
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onEdit
        ) {
            Text("Edit item")
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Button(
            modifier =
                Modifier.fillMaxWidth(),

            onClick = onBack
        ) {
            Text("Back")
        }
    }
}