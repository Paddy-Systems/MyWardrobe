package com.paddysystems.mywardrobe.ui.screens.additem

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.activity.result.contract.ActivityResultContracts.TakePicture
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
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
import com.paddysystems.mywardrobe.createCameraImageUri

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

    val galleryPicker = rememberLauncherForActivityResult(
        contract = PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            selectedImageUri = uri
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = TakePicture()
    ) { success ->
        if (success) {
            selectedImageUri = pendingCameraUri
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Add Item")

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        if (selectedImageUri == null) {
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
        } else {
            AsyncImage(
                model = selectedImageUri,
                contentDescription = "Selected wardrobe item",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    selectedImageUri = null
                }
            ) {
                Text("Choose another photo")
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