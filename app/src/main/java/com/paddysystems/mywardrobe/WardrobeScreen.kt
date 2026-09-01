package com.paddysystems.mywardrobe

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paddysystems.mywardrobe.ui.theme.MyWardrobeTheme
import java.io.File
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.height

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton

import com.paddysystems.mywardrobe.ui.components.WardrobeGrid
import com.paddysystems.mywardrobe.ui.components.WardrobeToolbar
import com.paddysystems.mywardrobe.ui.components.SelectionActions

@Composable
fun WardrobeScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    var searchQuery by remember {
        mutableStateOf("")
    }

    val selectedImages = remember {
        mutableStateListOf<File>()
    }

    var showDeleteConfirmation by remember {
        mutableStateOf(false)
    }

    val images = remember {
        mutableStateListOf<File>().apply {
            addAll(loadImages(context))
        }
    }

    val imagePicker = rememberLauncherForActivityResult(
        contract = PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val savedImage = saveImage(context, uri)

            if (savedImage != null) {
                images.add(savedImage)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = if (selectedImages.isEmpty()) {
                "My Wardrobe · ${images.size} items"
            } else {
                "${selectedImages.size} selected"
            },
            style = MaterialTheme.typography.headlineLarge
        )
        SelectionActions(
            selectedCount = selectedImages.size,
            onCancel = {
                selectedImages.clear()
            },
            onDelete = {
                showDeleteConfirmation = true
            }
        )
        Spacer(
            modifier = Modifier.height(12.dp)
        )
        WardrobeToolbar(
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            onFilterClick = {
                //
            },
            onSortClick = {
                //
            }
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        WardrobeGrid(
            images = images,
            selectedImages = selectedImages,
            modifier = Modifier.weight(1f),
            onImageClick = { imageFile ->
                if (selectedImages.isNotEmpty()) {
                    if (selectedImages.contains(imageFile)) {
                        selectedImages.remove(imageFile)
                    } else {
                        selectedImages.add(imageFile)
                    }
                }
            },
            onImageLongClick = { imageFile ->
                if (selectedImages.contains(imageFile)) {
                    selectedImages.remove(imageFile)
                } else {
                    selectedImages.add(imageFile)
                }
            }
        )

        if (showDeleteConfirmation) {
            AlertDialog(
                onDismissRequest = {
                    showDeleteConfirmation = false
                },
                title = {
                    Text("Remove ${selectedImages.size} items?")
                },
                text = {
                    Text(
                        "This will permanently remove ${selectedImages.size} items from your wardrobe."
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val imagesToDelete = selectedImages.toList()

                            imagesToDelete.forEach { imageFile ->
                                if (deleteImage(imageFile)) {
                                    images.remove(imageFile)
                                }
                            }

                            selectedImages.clear()
                            showDeleteConfirmation = false
                        }
                    ) {
                        Text("Remove")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showDeleteConfirmation = false
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                imagePicker.launch(
                    PickVisualMediaRequest(
                        PickVisualMedia.ImageOnly
                    )
                )
            }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("+")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add item")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WardrobePreview() {
    MyWardrobeTheme {
        WardrobeScreen()
    }
}