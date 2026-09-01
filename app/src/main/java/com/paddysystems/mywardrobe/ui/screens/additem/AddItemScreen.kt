package com.paddysystems.mywardrobe.ui.screens.additem

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.activity.result.contract.ActivityResultContracts.TakePicture
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.paddysystems.mywardrobe.data.storage.createCameraImageUri
import com.paddysystems.mywardrobe.domain.additem.FashionItemAnalyzer
import com.paddysystems.mywardrobe.domain.additem.WardrobeItemSaver
import com.paddysystems.mywardrobe.ui.components.EditorialPageHeader
import com.paddysystems.mywardrobe.ui.screens.additem.components.AddItemDetailsStep
import com.paddysystems.mywardrobe.ui.screens.additem.components.AddPhotoSourceStep
import com.paddysystems.mywardrobe.ui.screens.additem.components.AnalysisStatusCard
import kotlinx.coroutines.launch

@Composable
fun AddItemScreen(onBack: () -> Unit) {
    val context = LocalContext.current.applicationContext
    val scope = rememberCoroutineScope()
    val analyzer = remember(context) { FashionItemAnalyzer(context) }
    val saver = remember(context) { WardrobeItemSaver(context) }
    var state by remember { mutableStateOf(AddItemUiState()) }

    val galleryPicker = rememberLauncherForActivityResult(PickVisualMedia()) { uri ->
        uri?.let { state = state.copy(selectedImageUri = it, step = AddItemStep.ANALYSING, analysisFailed = false) }
    }
    val cameraLauncher = rememberLauncherForActivityResult(TakePicture()) { success ->
        if (success) state = state.copy(selectedImageUri = state.pendingCameraUri, step = AddItemStep.ANALYSING, analysisFailed = false)
    }

    LaunchedEffect(state.step, state.selectedImageUri) {
        if (state.step != AddItemStep.ANALYSING) return@LaunchedEffect
        val uri = state.selectedImageUri ?: return@LaunchedEffect
        runCatching { analyzer.analyze(uri) }
            .onSuccess { result ->
                state = state.copy(
                    step = AddItemStep.DETAILS,
                    imageEmbedding = result.embedding,
                    clothingTypeId = result.clothingMatches.firstOrNull()?.id,
                    colours = result.colourMatches.firstOrNull()?.let { listOf(it.id) }.orEmpty(),
                    metadata = result.metadata
                )
            }
            .onFailure {
                Log.e("FashionSigLIP", "Inference failed", it)
                state = state.copy(analysisFailed = true)
            }
    }

    Column(
        Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 22.dp)
    ) {
        EditorialPageHeader(
            eyebrow = state.step.headerEyebrow,
            title = state.step.headerTitle,
            subtitle = state.step.headerSubtitle,
            navigationIcon = Icons.AutoMirrored.Outlined.ArrowBack,
            onNavigate = onBack
        )
        Spacer(Modifier.height(24.dp))

        when (state.step) {
            AddItemStep.IMAGE -> AddPhotoSourceStep(
                onChoosePhoto = { galleryPicker.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly)) },
                onTakePhoto = {
                    val uri = createCameraImageUri(context)
                    state = state.copy(pendingCameraUri = uri)
                    cameraLauncher.launch(uri)
                }
            )
            AddItemStep.ANALYSING -> AnalysisStatusCard(state.analysisFailed) {
                state = AddItemUiState()
            }
            AddItemStep.DETAILS -> AddItemDetailsStep(
                imageUri = state.selectedImageUri,
                clothingTypeId = state.clothingTypeId,
                colours = state.colours,
                isSaving = state.isSaving,
                canSave = state.canSave,
                onTypeSelected = { state = state.copy(clothingTypeId = it) },
                onColoursChanged = { state = state.copy(colours = it) },
                onSave = {
                    val uri = state.selectedImageUri ?: return@AddItemDetailsStep
                    val type = state.clothingTypeId ?: return@AddItemDetailsStep
                    val embedding = state.imageEmbedding ?: return@AddItemDetailsStep
                    state = state.copy(isSaving = true)
                    scope.launch {
                        if (saver.save(uri, type, state.colours, embedding, state.metadata) != null) onBack()
                        else state = state.copy(isSaving = false)
                    }
                }
            )
        }
    }
}
