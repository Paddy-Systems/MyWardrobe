package com.paddysystems.mywardrobe.ui.screens.edititem

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
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.paddysystems.mywardrobe.data.storage.deleteWardrobeItem
import com.paddysystems.mywardrobe.data.storage.loadOutfitsUsingItem
import com.paddysystems.mywardrobe.data.storage.loadWardrobeItem
import com.paddysystems.mywardrobe.data.storage.updateWardrobeItem
import com.paddysystems.mywardrobe.ui.LocalActiveProfile
import com.paddysystems.mywardrobe.ui.components.DeleteConfirmationDialog
import com.paddysystems.mywardrobe.ui.components.EditorialDangerButton
import com.paddysystems.mywardrobe.ui.components.EditorialPageHeader
import com.paddysystems.mywardrobe.ui.components.EditorialPrimaryButton
import com.paddysystems.mywardrobe.ui.screens.additem.components.ClothingTypeSelector
import com.paddysystems.mywardrobe.ui.screens.additem.components.ColourSelector
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun EditItemScreen(
    itemId: String,
    onBack: () -> Unit,
    onSaved: () -> Unit,
    onDeleted: () -> Unit
) {
    val context = LocalContext.current
    val profileId = LocalActiveProfile.current.id
    val scope = rememberCoroutineScope()

    val item = remember(itemId, profileId) {
        loadWardrobeItem(
            context = context,
            profileId = profileId,
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

    var selectedClothingTypeId by remember(itemId) {
        mutableStateOf(item.clothingTypeId)
    }

    var selectedColours by remember(itemId) {
        mutableStateOf(item.colours)
    }

    var showDeleteDialog by remember {
        mutableStateOf(false)
    }

    var isDeleting by remember {
        mutableStateOf(false)
    }

    var deleteError by remember {
        mutableStateOf<String?>(null)
    }

    val affectedOutfitCount = remember(itemId, profileId) {
        loadOutfitsUsingItem(
            context = context,
            profileId = profileId,
            itemId = itemId
        ).size
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

        Spacer(Modifier.height(16.dp))

        Surface(
            shape = RoundedCornerShape(26.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            AsyncImage(
                model = item.cutoutPath?.let(::File)?.takeIf(File::exists) ?: File(item.imagePath),
                contentDescription = "Wardrobe item",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .padding(18.dp)
            )
        }

        Spacer(Modifier.height(16.dp))

        ClothingTypeSelector(
            selectedTypeId = selectedClothingTypeId,
            onTypeSelected = { typeId ->
                selectedClothingTypeId = typeId
            }
        )

        Spacer(Modifier.height(8.dp))

        ColourSelector(
            selectedColours = selectedColours,
            onColoursChanged = { colours ->
                selectedColours = colours
            }
        )

        Spacer(Modifier.height(24.dp))

        EditorialPrimaryButton(
            text = "Save changes",
            icon = Icons.Outlined.Check,
            modifier = Modifier.fillMaxWidth(),
            enabled = selectedColours.isNotEmpty() && !isDeleting,
            onClick = {
                scope.launch {
                    val saved = withContext(Dispatchers.IO) {
                        updateWardrobeItem(
                            context = context.applicationContext,
                            profileId = profileId,
                            item = item,
                            clothingTypeId = selectedClothingTypeId,
                            colours = selectedColours
                        )
                    }

                    if (saved) {
                        onSaved()
                    }
                }
            }
        )

        Spacer(Modifier.height(12.dp))

        EditorialDangerButton(
            text = "Remove from wardrobe",
            icon = Icons.Outlined.DeleteOutline,
            enabled = !isDeleting,
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                showDeleteDialog = true
            }
        )

        deleteError?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }

    if (showDeleteDialog) {
        DeleteConfirmationDialog(
            selectedCount = 1,
            affectedOutfitCount = affectedOutfitCount,
            onDismiss = {
                showDeleteDialog = false
            },
            onConfirm = {
                showDeleteDialog = false
                isDeleting = true
                deleteError = null

                scope.launch {
                    val deleted = withContext(Dispatchers.IO) {
                        deleteWardrobeItem(
                            context = context.applicationContext,
                            profileId = profileId,
                            item = item
                        )
                    }

                    isDeleting = false

                    if (deleted) {
                        onDeleted()
                    } else {
                        deleteError = "Could not remove this item."
                    }
                }
            }
        )
    }
}
