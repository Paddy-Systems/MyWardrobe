package com.paddysystems.mywardrobe.ui.screens.additem.components

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paddysystems.mywardrobe.ui.components.EditorialPrimaryButton

@Composable
fun AddItemDetailsStep(
    imageUri: Uri?,
    clothingTypeId: String?,
    colours: List<String>,
    isSaving: Boolean,
    canSave: Boolean,
    onTypeSelected: (String) -> Unit,
    onColoursChanged: (List<String>) -> Unit,
    onSave: () -> Unit
) {
    Column {
        imageUri?.let { ItemImagePreview(it); Spacer(Modifier.height(16.dp)) }
        Text("Item details", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        ClothingTypeSelector(clothingTypeId, onTypeSelected)
        Spacer(Modifier.height(8.dp))
        ColourSelector(colours, onColoursChanged)
        Spacer(Modifier.height(16.dp))
        EditorialPrimaryButton(if (isSaving) "Preparing cut-out…" else "Save to wardrobe", onSave, Modifier.fillMaxWidth(), canSave)
    }
}
