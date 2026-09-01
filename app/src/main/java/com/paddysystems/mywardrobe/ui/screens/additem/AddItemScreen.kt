package com.paddysystems.mywardrobe.ui.screens.additem

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.activity.result.contract.ActivityResultContracts.TakePicture
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.paddysystems.mywardrobe.createCameraImageUri

import android.util.Log
import androidx.compose.runtime.LaunchedEffect
import com.paddysystems.mywardrobe.ml.FashionSigLipEncoder

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.paddysystems.mywardrobe.ml.ClothingEmbeddingMatcher
import com.paddysystems.mywardrobe.ml.ColourEmbeddingMatcher
import com.paddysystems.mywardrobe.ui.screens.additem.components.ItemImagePreview
import com.paddysystems.mywardrobe.ui.screens.additem.components.ClothingTypeSelector
import com.paddysystems.mywardrobe.ui.screens.additem.components.ColourSelector

import androidx.compose.runtime.rememberCoroutineScope
import com.paddysystems.mywardrobe.data.storage.saveWardrobeItem
import kotlinx.coroutines.launch

import com.paddysystems.mywardrobe.data.model.WardrobeMetadata
import com.paddysystems.mywardrobe.ml.FashionAnalysisResult
import com.paddysystems.mywardrobe.ml.SemanticMetadataAnalyzer
import com.paddysystems.mywardrobe.data.storage.WardrobeCutoutService
import com.paddysystems.mywardrobe.ui.components.EditorialPageHeader
import com.paddysystems.mywardrobe.ui.components.EditorialPrimaryButton
import com.paddysystems.mywardrobe.ui.components.EditorialSecondaryButton
@Composable
fun AddItemScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var selectedImageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    var pendingCameraUri by remember {
        mutableStateOf<Uri?>(null)
    }

    var currentStep by remember {
        mutableStateOf(AddItemStep.IMAGE)
    }

    var isSaving by remember {
        mutableStateOf(false)
    }

    var analysisFailed by remember { mutableStateOf(false) }

    val galleryPicker = rememberLauncherForActivityResult(
        contract = PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            currentStep = AddItemStep.ANALYSING
        }
    }

    var imageEmbedding by remember {
        mutableStateOf<FloatArray?>(null)
    }

    var predictedMetadata by remember {
        mutableStateOf(
            WardrobeMetadata()
        )
    }

    var predictedClothingTypeId by remember {
        mutableStateOf<String?>(null)
    }

    var predictedColours by remember {
        mutableStateOf<List<String>>(emptyList())
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = TakePicture()
    ) { success ->
        if (success) {
            selectedImageUri = pendingCameraUri
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
            eyebrow = when (currentStep) {
                AddItemStep.IMAGE -> "A new piece"
                AddItemStep.ANALYSING -> "Smart wardrobe"
                AddItemStep.DETAILS -> "Review & refine"
            },
            title = when (currentStep) {
                AddItemStep.IMAGE -> "Add to wardrobe"
                AddItemStep.ANALYSING -> "Reading your piece"
                AddItemStep.DETAILS -> "The finishing touches"
            },
            subtitle = when (currentStep) {
                AddItemStep.IMAGE -> "Choose a clear, well-lit photo. We’ll take care of the details."
                AddItemStep.ANALYSING -> "Identifying the garment, colour and best ways to style it."
                AddItemStep.DETAILS -> "Check our suggestions before saving it to your collection."
            },
            navigationIcon = Icons.Outlined.ArrowBack,
            onNavigate = onBack
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        when (currentStep) {

            AddItemStep.IMAGE -> {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(26.dp),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(Icons.Outlined.AutoAwesome, null, tint = MaterialTheme.colorScheme.secondary)
                        Text("Start with a photo", style = MaterialTheme.typography.titleLarge)
                        Text("A simple front-facing shot works best.", color = MaterialTheme.colorScheme.onSurfaceVariant)
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

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

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
            }

            AddItemStep.ANALYSING -> {

                LaunchedEffect(selectedImageUri) {
                    val imageUri = selectedImageUri
                        ?: return@LaunchedEffect

                    try {
                        val result =
                            withContext(Dispatchers.IO) {

                                val encoder =
                                    FashionSigLipEncoder(
                                        context.applicationContext
                                    )

                                val embedding =
                                    encoder.encode(imageUri)

                                val clothingMatcher =
                                    ClothingEmbeddingMatcher(
                                        context.applicationContext
                                    )

                                val colourMatcher =
                                    ColourEmbeddingMatcher(
                                        context.applicationContext
                                    )

                                val metadataAnalyzer =
                                    SemanticMetadataAnalyzer(
                                        context.applicationContext
                                    )

                                FashionAnalysisResult(
                                    embedding = embedding,

                                    clothingMatches =
                                        clothingMatcher.topMatches(
                                            embedding,
                                            5
                                        ),

                                    colourMatches =
                                        colourMatcher.topMatches(
                                            embedding,
                                            5
                                        ),

                                    metadata =
                                        metadataAnalyzer.analyze(
                                            embedding
                                        )
                                )
                            }
                        imageEmbedding =
                            result.embedding

                        predictedClothingTypeId =
                            result.clothingMatches
                                .firstOrNull()
                                ?.id

                        predictedColours =
                            result.colourMatches
                                .firstOrNull()
                                ?.let {
                                    listOf(it.id)
                                }
                                ?: emptyList()

                        predictedMetadata =
                            result.metadata

                        currentStep = AddItemStep.DETAILS

                        Log.d(
                            "FashionSigLIP",
                            buildString {
                                appendLine(
                                    "Semantic metadata:"
                                )

                                appendLine(
                                    "Patterns: ${
                                        result.metadata.patterns
                                            .joinToString {
                                                "${it.id}:${it.similarity}"
                                            }
                                    }"
                                )

                                appendLine(
                                    "Materials: ${
                                        result.metadata.materials
                                            .joinToString {
                                                "${it.id}:${it.similarity}"
                                            }
                                    }"
                                )

                                appendLine(
                                    "Styles: ${
                                        result.metadata.styles
                                            .joinToString {
                                                "${it.id}:${it.similarity}"
                                            }
                                    }"
                                )

                                appendLine(
                                    "Occasions: ${
                                        result.metadata.occasions
                                            .joinToString {
                                                "${it.id}:${it.similarity}"
                                            }
                                    }"
                                )

                                appendLine(
                                    "Seasons: ${
                                        result.metadata.seasons
                                            .joinToString {
                                                "${it.id}:${it.similarity}"
                                            }
                                    }"
                                )

                                appendLine(
                                    "Formalities: ${
                                        result.metadata.formalities
                                            .joinToString {
                                                "${it.id}:${it.similarity}"
                                            }
                                    }"
                                )
                            }
                        )
                    } catch (exception: Exception) {
                        Log.e(
                            "FashionSigLIP",
                            "Inference failed",
                            exception
                        )
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
                            Text("We couldn’t read that photo", style = MaterialTheme.typography.titleLarge)
                            Text("Try another image with the garment fully visible.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            EditorialSecondaryButton("Choose another", onClick = { currentStep = AddItemStep.IMAGE; analysisFailed = false })
                        } else {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.secondary)
                            Text("Analysing colour and cut…", style = MaterialTheme.typography.titleLarge)
                            Text("This usually takes a moment.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            AddItemStep.DETAILS -> {
                Column {
                    selectedImageUri?.let { imageUri ->
                        ItemImagePreview(
                            imageUri = imageUri
                        )

                        Spacer(
                            modifier = Modifier.height(16.dp)
                        )
                    }

                    Text("Item details", style = MaterialTheme.typography.titleLarge)

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    ClothingTypeSelector(
                        selectedTypeId = predictedClothingTypeId,
                        onTypeSelected = { typeId ->
                            predictedClothingTypeId = typeId
                        }
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    ColourSelector(
                        selectedColours = predictedColours,
                        onColoursChanged = { colours ->
                            predictedColours = colours
                        }
                    )

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )

                    EditorialPrimaryButton(
                        text = if (isSaving) "Preparing cut-out…" else "Save to wardrobe",
                        modifier = Modifier.fillMaxWidth(),
                        enabled =
                            !isSaving &&
                                    selectedImageUri != null &&
                                    predictedClothingTypeId != null &&
                                    predictedColours.isNotEmpty() &&
                                    imageEmbedding != null,
                        onClick = saveItem@ {
                            val imageUri =
                                selectedImageUri
                                    ?: return@saveItem

                            val clothingTypeId =
                                predictedClothingTypeId
                                    ?: return@saveItem

                            val embedding =
                                imageEmbedding
                                    ?: return@saveItem

                            isSaving = true

                            scope.launch {

                                val savedItem =
                                    withContext(
                                        Dispatchers.IO
                                    ) {

                                        val item =
                                            saveWardrobeItem(
                                                context =
                                                    context.applicationContext,

                                                imageUri =
                                                    imageUri,

                                                clothingTypeId =
                                                    clothingTypeId,

                                                colours =
                                                    predictedColours,

                                                imageEmbedding =
                                                    embedding,

                                                metadata =
                                                    predictedMetadata
                                            )
                                                ?: return@withContext null

                                        try {
                                            WardrobeCutoutService
                                                .ensureCutout(
                                                    context =
                                                        context.applicationContext,

                                                    item =
                                                        item
                                                )
                                        } catch (
                                            exception: Exception
                                        ) {
                                            /*
                                             * Cut-out generation failing
                                             * must NOT lose the wardrobe
                                             * item.
                                             *
                                             * We can regenerate it later.
                                             */
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

                                    Log.e(
                                        "AddItem",
                                        "Could not save wardrobe item"
                                    )
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}
