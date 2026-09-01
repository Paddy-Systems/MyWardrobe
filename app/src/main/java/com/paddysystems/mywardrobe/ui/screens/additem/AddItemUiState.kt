package com.paddysystems.mywardrobe.ui.screens.additem

import android.net.Uri
import com.paddysystems.mywardrobe.data.model.WardrobeMetadata

data class AddItemUiState(
    val selectedImageUri: Uri? = null,
    val pendingCameraUri: Uri? = null,
    val step: AddItemStep = AddItemStep.IMAGE,
    val isSaving: Boolean = false,
    val analysisFailed: Boolean = false,
    val imageEmbedding: FloatArray? = null,
    val metadata: WardrobeMetadata = WardrobeMetadata(),
    val clothingTypeId: String? = null,
    val colours: List<String> = emptyList()
) {
    val canSave: Boolean
        get() = !isSaving && selectedImageUri != null && clothingTypeId != null && colours.isNotEmpty() && imageEmbedding != null
}
