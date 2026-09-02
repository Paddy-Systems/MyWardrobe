package com.paddysystems.mywardrobe.ui.screens.outfits

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.paddysystems.mywardrobe.data.storage.deleteOutfit
import com.paddysystems.mywardrobe.data.storage.loadOutfit
import com.paddysystems.mywardrobe.data.storage.loadWardrobeItems
import com.paddysystems.mywardrobe.data.storage.renameOutfit
import com.paddysystems.mywardrobe.ui.LocalActiveProfile
import com.paddysystems.mywardrobe.ui.components.EditorialDangerButton
import com.paddysystems.mywardrobe.ui.components.EditorialPageHeader
import com.paddysystems.mywardrobe.ui.components.EditorialSecondaryButton
import com.paddysystems.mywardrobe.ui.screens.createoutfit.components.StackedOutfitPreview
import com.paddysystems.mywardrobe.ui.screens.outfits.components.DeleteOutfitDialog
import com.paddysystems.mywardrobe.ui.screens.outfits.components.OutfitFinishingPieces
import com.paddysystems.mywardrobe.ui.screens.outfits.components.RenameOutfitDialog
import com.paddysystems.mywardrobe.ui.screens.outfits.components.SavedOutfitLayerPreview
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun OutfitDetailsScreen(
    outfitId: String,
    refreshKey: Int = 0,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onItemClick: (String) -> Unit = {},
    onChanged: () -> Unit,
    onDeleted: () -> Unit
) {
    val context = LocalContext.current
    val profileId = LocalActiveProfile.current.id
    val scope = rememberCoroutineScope()

    var outfit by remember(outfitId, refreshKey, profileId) {
        mutableStateOf(loadOutfit(context, profileId, outfitId))
    }

    val wardrobeItems = remember(refreshKey, profileId) {
        loadWardrobeItems(context, profileId)
    }

    var selectedLayerIndex by remember { mutableStateOf<Int?>(null) }
    var showRenameDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var isWorking by remember { mutableStateOf(false) }
    var actionError by remember { mutableStateOf<String?>(null) }

    val current = outfit

    if (current == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(20.dp)
        ) {
            EditorialPageHeader(
                eyebrow = "Saved fit",
                title = "Fit unavailable",
                subtitle = "This saved fit could not be loaded.",
                navigationIcon = Icons.AutoMirrored.Outlined.ArrowBack,
                onNavigate = onBack
            )
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(top = 22.dp, bottom = 28.dp)
    ) {
        EditorialPageHeader(
            eyebrow = "Saved fit",
            title = current.name,
            subtitle = "${current.layers.size} ${if (current.layers.size == 1) "layer" else "layers"}",
            navigationIcon = Icons.AutoMirrored.Outlined.ArrowBack,
            onNavigate = onBack,
            actionIcon = Icons.Outlined.Edit,
            onAction = onEdit,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(Modifier.height(22.dp))

        if (current.layers.isEmpty()) {
            Text(
                text = "NO BODY LAYERS",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Text(
                text = "This fit only contains finishing pieces.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )
        } else {
            val layerIndex = selectedLayerIndex

            if (layerIndex == null) {
                Text(
                    text = "LAYER STACK",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )

                Spacer(Modifier.height(8.dp))

                StackedOutfitPreview(
                    layers = current.layers,
                    wardrobeItems = wardrobeItems,
                    onLayerClick = { selectedLayerIndex = it },
                    modifier = Modifier.padding(horizontal = 20.dp)
                )

                Text(
                    text = "Tap a layer to inspect it.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
            } else {
                val safeIndex = layerIndex.coerceIn(0, current.layers.lastIndex)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    Text(
                        text = "LAYER ${safeIndex + 1} OF ${current.layers.size}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = "STACKED",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable { selectedLayerIndex = null }
                    )
                }

                Spacer(Modifier.height(8.dp))

                SavedOutfitLayerPreview(
                    layer = current.layers[safeIndex],
                    layerNumber = safeIndex + 1,
                    wardrobeItems = wardrobeItems,
                    onItemClick = onItemClick,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    EditorialSecondaryButton(
                        text = "Previous",
                        enabled = safeIndex > 0,
                        onClick = { selectedLayerIndex = safeIndex - 1 },
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(Modifier.width(8.dp))

                    EditorialSecondaryButton(
                        text = "Next",
                        enabled = safeIndex < current.layers.lastIndex,
                        onClick = { selectedLayerIndex = safeIndex + 1 },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(Modifier.height(26.dp))

        OutfitFinishingPieces(
            outfit = current,
            wardrobeItems = wardrobeItems,
            onItemClick = onItemClick,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(Modifier.height(30.dp))

        EditorialSecondaryButton(
            text = "Rename fit",
            icon = Icons.Outlined.Edit,
            enabled = !isWorking,
            onClick = { showRenameDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        )

        Spacer(Modifier.height(10.dp))

        EditorialDangerButton(
            text = "Delete fit",
            icon = Icons.Outlined.DeleteOutline,
            enabled = !isWorking,
            onClick = { showDeleteDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        )

        actionError?.let { error ->
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
            )
        }
    }

    if (showRenameDialog) {
        RenameOutfitDialog(
            currentName = current.name,
            onDismiss = { showRenameDialog = false },
            onRename = { name ->
                showRenameDialog = false
                isWorking = true
                actionError = null

                scope.launch {
                    val updated = withContext(Dispatchers.IO) {
                        renameOutfit(
                            context = context.applicationContext,
                            profileId = profileId,
                            outfitId = current.id,
                            name = name
                        )
                    }

                    isWorking = false
                    if (updated != null) {
                        outfit = updated
                        onChanged()
                    } else {
                        actionError = "Could not rename fit."
                    }
                }
            }
        )
    }

    if (showDeleteDialog) {
        DeleteOutfitDialog(
            outfitName = current.name,
            onDismiss = { showDeleteDialog = false },
            onConfirm = {
                showDeleteDialog = false
                isWorking = true
                actionError = null

                scope.launch {
                    val deleted = withContext(Dispatchers.IO) {
                        deleteOutfit(
                            context = context.applicationContext,
                            profileId = profileId,
                            outfitId = current.id
                        )
                    }

                    isWorking = false
                    if (deleted) {
                        onDeleted()
                    } else {
                        actionError = "Could not delete fit."
                    }
                }
            }
        )
    }
}
