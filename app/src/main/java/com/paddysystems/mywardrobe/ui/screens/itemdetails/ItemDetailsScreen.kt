package com.paddysystems.mywardrobe.ui.screens.itemdetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.paddysystems.mywardrobe.data.model.defaultClothingTypes
import com.paddysystems.mywardrobe.data.model.wardrobeLabel
import com.paddysystems.mywardrobe.data.storage.loadOutfitsUsingItem
import com.paddysystems.mywardrobe.data.storage.loadWardrobeItem
import com.paddysystems.mywardrobe.data.storage.loadWardrobeItems
import com.paddysystems.mywardrobe.ui.components.EditorialPageHeader
import com.paddysystems.mywardrobe.ui.components.EditorialPrimaryButton
import com.paddysystems.mywardrobe.ui.screens.itemdetails.components.ItemOutfitUsageSection
import com.paddysystems.mywardrobe.ui.screens.itemdetails.components.SemanticMetadataSection
import java.io.File

@Composable
fun ItemDetailsScreen(
    itemId: String,
    refreshKey: Int = 0,
    onEdit: () -> Unit,
    onOutfitClick: (String) -> Unit = {},
    onBack: () -> Unit
) {
    val context = LocalContext.current

    val item = remember(itemId, refreshKey) {
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
            Spacer(Modifier.height(16.dp))
            Button(onClick = onBack) {
                Text("Back")
            }
        }
        return
    }

    val outfits = remember(itemId, refreshKey) {
        loadOutfitsUsingItem(
            context = context,
            itemId = itemId
        )
    }

    val wardrobeItems = remember(refreshKey) {
        loadWardrobeItems(context)
    }

    val cutoutFile = item.cutoutPath
        ?.let(::File)
        ?.takeIf { it.exists() }

    val clothingTypeName = defaultClothingTypes
        .firstOrNull { it.id == item.clothingTypeId }
        ?.name
        ?: item.clothingTypeId

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 22.dp)
    ) {
        EditorialPageHeader(
            eyebrow = "Wardrobe piece",
            title = clothingTypeName,
            subtitle = "Your saved styling details",
            navigationIcon = Icons.Outlined.ArrowBack,
            onNavigate = onBack
        )

        Spacer(Modifier.height(16.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(26.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            AsyncImage(
                model = cutoutFile ?: File(item.imagePath),
                contentDescription = "Wardrobe item",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp)
                    .padding(18.dp)
            )
        }

        Spacer(Modifier.height(16.dp))

        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Text(
                    "AT A GLANCE",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(10.dp))
                Text(clothingTypeName, style = MaterialTheme.typography.titleLarge)
                Text(
                    item.colours.joinToString("  ·  ") { wardrobeLabel(it) },
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        SemanticMetadataSection(metadata = item.metadata)

        Spacer(Modifier.height(24.dp))

        ItemOutfitUsageSection(
            outfits = outfits,
            wardrobeItems = wardrobeItems,
            onOutfitClick = onOutfitClick
        )

        Spacer(Modifier.height(24.dp))

        EditorialPrimaryButton(
            text = "Edit item",
            icon = Icons.Outlined.Edit,
            modifier = Modifier.fillMaxWidth(),
            onClick = onEdit
        )
    }
}
