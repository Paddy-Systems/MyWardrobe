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
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text("Add Item")

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        when (currentStep) {

            AddItemStep.IMAGE -> {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        galleryPicker.launch(
                            PickVisualMediaRequest(
                                PickVisualMedia.ImageOnly
                            )
                        )
                    }
                ) {
                    Text("Choose photo")
                }

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        val uri = createCameraImageUri(context)

                        pendingCameraUri = uri
                        cameraLauncher.launch(uri)
                    }
                ) {
                    Text("Take photo")
                }
            }

            AddItemStep.ANALYSING -> {

                LaunchedEffect(selectedImageUri) {
                    val imageUri = selectedImageUri
                        ?: return@LaunchedEffect

                    try {
                        val (
                            embedding,
                            clothingMatches,
                            colourMatches
                        ) = withContext(Dispatchers.IO) {
                                val encoder = FashionSigLipEncoder(
                                    context.applicationContext
                                )

                                val embedding = encoder.encode(imageUri)



                                val clothingMatcher = ClothingEmbeddingMatcher(
                                    context.applicationContext
                                )

                                val colourMatcher = ColourEmbeddingMatcher(
                                    context.applicationContext
                                )

                            Triple(
                                embedding,

                                clothingMatcher.topMatches(
                                    embedding,
                                    5
                                ),

                                colourMatcher.topMatches(
                                    embedding,
                                    5
                                )
                            )

                            }
                        Log.d(
                            "FashionSigLIP",
                            buildString {
                                appendLine("Top clothing matches:")

                                clothingMatches.forEach { match ->
                                    appendLine(
                                        "${match.id}: ${match.similarity}"
                                    )
                                }
                            }
                        )
                        Log.d(
                            "FashionSigLIP",
                            buildString {
                                appendLine("Top colour matches:")

                                colourMatches.forEach { match ->
                                    appendLine(
                                        "${match.id}: ${match.similarity}"
                                    )
                                }
                            }
                        )
                        imageEmbedding =
                            embedding
                        predictedClothingTypeId =
                            clothingMatches.firstOrNull()?.id

                        predictedColours =
                            colourMatches
                                .firstOrNull()
                                ?.let { listOf(it.id) }
                                ?: emptyList()

                        currentStep = AddItemStep.DETAILS
                    } catch (exception: Exception) {
                        Log.e(
                            "FashionSigLIP",
                            "Inference failed",
                            exception
                        )
                    }
                }

                Column {
                    Text("Analysing item...")
                    Text("Loading FashionSigLIP...")
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

                    Text("Item details")

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

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        enabled =
                            selectedImageUri != null &&
                                    predictedClothingTypeId != null &&
                                    predictedColours.isNotEmpty() &&
                                    imageEmbedding != null,
                        onClick = {
                            val imageUri =
                                selectedImageUri
                                    ?: return@Button

                            val clothingTypeId =
                                predictedClothingTypeId
                                    ?: return@Button

                            val embedding =
                                imageEmbedding
                                    ?: return@Button

                            scope.launch {
                                val savedItem =
                                    withContext(Dispatchers.IO) {
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
                                                embedding
                                        )
                                    }

                                if (savedItem != null) {
                                    onBack()
                                } else {
                                    Log.e(
                                        "AddItem",
                                        "Could not save wardrobe item"
                                    )
                                }
                            }
                        }
                    ) {
                        Text("Save item")
                    }
                }
            }
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = onBack
        ) {
            Text("Back")
        }
    }
}