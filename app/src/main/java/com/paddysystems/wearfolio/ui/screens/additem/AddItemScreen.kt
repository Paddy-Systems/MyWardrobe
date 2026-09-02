package com.paddysystems.wearfolio.ui.screens.additem

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickMultipleVisualMedia
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.activity.result.contract.ActivityResultContracts.TakePicture
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.paddysystems.wearfolio.createCameraImageUri
import com.paddysystems.wearfolio.data.model.WardrobeMetadata
import com.paddysystems.wearfolio.data.storage.WardrobeCutoutService
import com.paddysystems.wearfolio.data.storage.WardrobeImportQueue
import com.paddysystems.wearfolio.data.storage.saveWardrobeItem
import com.paddysystems.wearfolio.ml.WardrobeAnalysisService
import com.paddysystems.wearfolio.ui.LocalActiveProfile
import com.paddysystems.wearfolio.ui.components.EditorialPageHeader
import com.paddysystems.wearfolio.ui.components.EditorialPrimaryButton
import com.paddysystems.wearfolio.ui.components.EditorialSecondaryButton
import com.paddysystems.wearfolio.ui.screens.additem.components.ClothingTypeSelector
import com.paddysystems.wearfolio.ui.screens.additem.components.ColourSelector
import com.paddysystems.wearfolio.ui.screens.additem.components.ItemImagePreview
import com.paddysystems.wearfolio.worker.WardrobeBatchImporter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun AddItemScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val profileId = LocalActiveProfile.current.id
    val scope = rememberCoroutineScope()

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }
    var currentStep by remember { mutableStateOf(AddItemStep.IMAGE) }
    var isSaving by remember { mutableStateOf(false) }
    var analysisFailed by remember { mutableStateOf(false) }
    var isQueueingBatch by remember { mutableStateOf(false) }
    var batchQueueError by remember { mutableStateOf<String?>(null) }

    var imageEmbedding by remember { mutableStateOf<FloatArray?>(null) }
    var predictedMetadata by remember { mutableStateOf(WardrobeMetadata()) }
    var predictedClothingTypeId by remember { mutableStateOf<String?>(null) }
    var predictedColours by remember { mutableStateOf<List<String>>(emptyList()) }

    BackHandler(enabled = isQueueingBatch) {
        // The selected gallery URIs are only guaranteed while this screen is
        // alive. Keep the screen mounted until every photo has been copied
        // into private app storage.
    }

    val galleryPicker = rememberLauncherForActivityResult(
        contract = PickMultipleVisualMedia()
    ) { uris ->
        when {
            uris.isEmpty() -> Unit

            uris.size == 1 -> {
                selectedImageUri = uris.first()
                analysisFailed = false
                currentStep = AddItemStep.ANALYSING
            }

            else -> {
                isQueueingBatch = true
                batchQueueError = null

                scope.launch {
                    val staged = withContext(Dispatchers.IO) {
                        WardrobeImportQueue.stageImages(
                            context = context.applicationContext,
                            profileId = profileId,
                            imageUris = uris
                        )
                    }

                    if (staged.isEmpty()) {
                        isQueueingBatch = false
                        batchQueueError = "We couldn't add those photos. Please try again."
                        return@launch
                    }

                    WardrobeBatchImporter.enqueue(
                        context = context.applicationContext,
                        profileId = profileId,
                        importIds = staged.map { it.id }
                    )

                    if (staged.size != uris.size) {
                        Toast.makeText(
                            context,
                            "Added ${staged.size} of ${uris.size} photos. The rest could not be copied.",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    isQueueingBatch = false
                    onBack()
                }
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = TakePicture()
    ) { success ->
        if (success) {
            selectedImageUri = pendingCameraUri
            analysisFailed = false
            currentStep = AddItemStep.ANALYSING
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 22.dp),
        verticalArrangement = Arrangement.Top
    ) {
        EditorialPageHeader(
            eyebrow = when {
                isQueueingBatch -> "Batch add"
                currentStep == AddItemStep.IMAGE -> "A new piece"
                currentStep == AddItemStep.ANALYSING -> "Smart wardrobe"
                else -> "Review & refine"
            },
            title = when {
                isQueueingBatch -> "Adding your photos"
                currentStep == AddItemStep.IMAGE -> "Add to wardrobe"
                currentStep == AddItemStep.ANALYSING -> "Reading your piece"
                else -> "The finishing touches"
            },
            subtitle = when {
                isQueueingBatch -> "Copying your selection safely before the wardrobe processes each piece."
                currentStep == AddItemStep.IMAGE -> "Choose one photo for the guided flow, or several to add them automatically."
                currentStep == AddItemStep.ANALYSING -> "Identifying the garment, colour and best ways to style it."
                else -> "Check our suggestions before saving it to your collection."
            },
            navigationIcon = if (isQueueingBatch) null else Icons.Outlined.ArrowBack,
            onNavigate = if (isQueueingBatch) null else onBack
        )

        Spacer(Modifier.height(24.dp))

        if (isQueueingBatch) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier.padding(28.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        "Preparing your batch…",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        "Once the photos are safely in your wardrobe, you can leave this screen while they are analysed one at a time.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            return@Column
        }

        when (currentStep) {
            AddItemStep.IMAGE -> {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(26.dp),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            Icons.Outlined.AutoAwesome,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            "Start with a photo",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            "Select one for the usual review step, or choose several and we'll add them in the background.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(Modifier.height(18.dp))

                EditorialPrimaryButton(
                    text = "Choose from photos",
                    icon = Icons.Outlined.PhotoLibrary,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        galleryPicker.launch(
                            PickVisualMediaRequest(
                                PickVisualMedia.ImageOnly
                            )
                        )
                    }
                )

                Spacer(Modifier.height(12.dp))

                EditorialSecondaryButton(
                    text = "Take a photo",
                    icon = Icons.Outlined.PhotoCamera,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        val uri = createCameraImageUri(context)
                        pendingCameraUri = uri
                        cameraLauncher.launch(uri)
                    }
                )

                batchQueueError?.let { message ->
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            AddItemStep.ANALYSING -> {
                LaunchedEffect(selectedImageUri) {
                    val imageUri = selectedImageUri ?: return@LaunchedEffect

                    try {
                        val result = WardrobeAnalysisService.analyse(
                            context = context.applicationContext,
                            imageUri = imageUri
                        )

                        imageEmbedding = result.embedding
                        predictedClothingTypeId = result.clothingMatches
                            .firstOrNull()
                            ?.id
                        predictedColours = result.colourMatches
                            .firstOrNull()
                            ?.let { listOf(it.id) }
                            ?: emptyList()
                        predictedMetadata = result.metadata
                        currentStep = AddItemStep.DETAILS

                        Log.d(
                            "FashionSigLIP",
                            "Analysed ${predictedClothingTypeId ?: "unknown garment"}; " +
                                "styles=${result.metadata.styles.joinToString { it.id }}"
                        )
                    } catch (exception: Exception) {
                        Log.e("FashionSigLIP", "Inference failed", exception)
                        analysisFailed = true
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(26.dp),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        if (analysisFailed) {
                            Text(
                                "We couldn’t read that photo",
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                "Try another image with the garment fully visible.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            EditorialSecondaryButton(
                                text = "Choose another",
                                onClick = {
                                    currentStep = AddItemStep.IMAGE
                                    analysisFailed = false
                                }
                            )
                        } else {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                "Analysing colour and cut…",
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                "This usually takes a moment.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            AddItemStep.DETAILS -> {
                Column {
                    selectedImageUri?.let { imageUri ->
                        ItemImagePreview(imageUri = imageUri)
                        Spacer(Modifier.height(16.dp))
                    }

                    Text(
                        "Item details",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(Modifier.height(8.dp))

                    ClothingTypeSelector(
                        selectedTypeId = predictedClothingTypeId,
                        onTypeSelected = { typeId ->
                            predictedClothingTypeId = typeId
                        }
                    )

                    Spacer(Modifier.height(8.dp))

                    ColourSelector(
                        selectedColours = predictedColours,
                        onColoursChanged = { colours ->
                            predictedColours = colours
                        }
                    )

                    Spacer(Modifier.height(16.dp))

                    EditorialPrimaryButton(
                        text = if (isSaving) {
                            "Preparing cut-out…"
                        } else {
                            "Save to wardrobe"
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled =
                            !isSaving &&
                                selectedImageUri != null &&
                                predictedClothingTypeId != null &&
                                predictedColours.isNotEmpty() &&
                                imageEmbedding != null,
                        onClick = saveItem@ {
                            val imageUri = selectedImageUri ?: return@saveItem
                            val clothingTypeId = predictedClothingTypeId ?: return@saveItem
                            val embedding = imageEmbedding ?: return@saveItem

                            isSaving = true

                            scope.launch {
                                val savedItem = withContext(Dispatchers.IO) {
                                    val item = saveWardrobeItem(
                                        context = context.applicationContext,
                                        profileId = profileId,
                                        imageUri = imageUri,
                                        clothingTypeId = clothingTypeId,
                                        colours = predictedColours,
                                        imageEmbedding = embedding,
                                        metadata = predictedMetadata
                                    ) ?: return@withContext null

                                    try {
                                        WardrobeCutoutService.ensureCutout(
                                            context = context.applicationContext,
                                            profileId = profileId,
                                            item = item
                                        )
                                    } catch (exception: Exception) {
                                        Log.e(
                                            "ISNet",
                                            "Could not generate cut-out",
                                            exception
                                        )
                                        item
                                    }
                                }

                                if (savedItem != null) {
                                    onBack()
                                } else {
                                    isSaving = false
                                    Log.e("AddItem", "Could not save wardrobe item")
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}
