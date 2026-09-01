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

import com.paddysystems.mywardrobe.data.model.CropType
import com.paddysystems.mywardrobe.ui.screens.additem.components.CropStep
import com.paddysystems.mywardrobe.ui.screens.additem.components.ItemShapeStep

@Composable
fun AddItemScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current

    var selectedImageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    var pendingCameraUri by remember {
        mutableStateOf<Uri?>(null)
    }

    var currentStep by remember {
        mutableStateOf(AddItemStep.IMAGE)
    }

    var cropType by remember {
        mutableStateOf<CropType?>(null)
    }

    val galleryPicker = rememberLauncherForActivityResult(
        contract = PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            currentStep = AddItemStep.ANALYSING
        }
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
                Column {
                    Text("Analysing item...")
                    Text("Detecting clothing type and colours")
                }
            }

            AddItemStep.CROP -> {
                val imageUri = selectedImageUri
                val selectedCropType = cropType

                if (
                    imageUri != null &&
                    selectedCropType != null
                ) {
                    CropStep(
                        imageUri = imageUri,
                        cropType = selectedCropType,
                        onBack = {
                            currentStep = AddItemStep.SHAPE
                        },
                        onContinue = {
                            //
                        }
                    )
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