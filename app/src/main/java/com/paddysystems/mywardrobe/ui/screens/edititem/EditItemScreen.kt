package com.paddysystems.mywardrobe.ui.screens.edititem

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.paddysystems.mywardrobe.data.storage.loadWardrobeItem
import com.paddysystems.mywardrobe.ui.screens.additem.components.ClothingTypeSelector
import com.paddysystems.mywardrobe.ui.screens.additem.components.ColourSelector
import java.io.File

import androidx.compose.runtime.rememberCoroutineScope
import com.paddysystems.mywardrobe.data.storage.updateWardrobeItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Check
import com.paddysystems.mywardrobe.ui.components.EditorialPageHeader
import com.paddysystems.mywardrobe.ui.components.EditorialPrimaryButton

@Composable
fun EditItemScreen(
    itemId: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

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

    var selectedClothingTypeId by remember(itemId) {
        mutableStateOf(
            item.clothingTypeId
        )
    }

    var selectedColours by remember(itemId) {
        mutableStateOf(
            item.colours
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 22.dp)
    ) {
        EditorialPageHeader(
            eyebrow = "Wardrobe piece",
            title = "Edit details",
            subtitle = "Keep the information that makes outfit suggestions useful.",
            navigationIcon = Icons.Outlined.ArrowBack,
            onNavigate = onBack
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Surface(shape = RoundedCornerShape(26.dp), color = MaterialTheme.colorScheme.surface) {
            AsyncImage(
                model = item.cutoutPath?.let(::File)?.takeIf(File::exists) ?: File(item.imagePath),
                contentDescription = "Wardrobe item",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxWidth().height(280.dp).padding(18.dp)
            )
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        ClothingTypeSelector(
            selectedTypeId = selectedClothingTypeId,
            onTypeSelected = { typeId ->
                selectedClothingTypeId = typeId
            }
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        ColourSelector(
            selectedColours = selectedColours,
            onColoursChanged = { colours ->
                selectedColours = colours
            }
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        EditorialPrimaryButton(
            text = "Save changes",
            icon = Icons.Outlined.Check,
            modifier = Modifier.fillMaxWidth(),
            enabled = selectedColours.isNotEmpty(),
            onClick = {
                scope.launch {
                    val saved =
                        withContext(Dispatchers.IO) {
                            updateWardrobeItem(
                                context = context.applicationContext,
                                item = item,
                                clothingTypeId =
                                    selectedClothingTypeId,
                                colours =
                                    selectedColours
                            )
                        }

                    if (saved) {
                        onBack()
                    }
                }
            }
        )
    }
}
