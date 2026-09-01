package com.paddysystems.mywardrobe

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.paddysystems.mywardrobe.ui.theme.MyWardrobeTheme
import java.io.File
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton

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
        if (selectedImages.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        selectedImages.clear()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                ) {
                    Text("Cancel")
                }

                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        showDeleteConfirmation = true
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                ) {
                    Text("Delete ${selectedImages.size}")
                }
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )
        }
        Spacer(
            modifier = Modifier.height(12.dp)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text("Search")
                },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )
            Button(
                modifier = Modifier.height(56.dp),
                onClick = {
                    //
                },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Text("Filter")
            }
            Button(
                modifier = Modifier.height(56.dp),
                onClick = {
                    //
                },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Text("Sort")
            }
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        if (images.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("No items yet")
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(images) { imageFile ->
                    val isSelected = selectedImages.contains(imageFile)

                    AsyncImage(
                        model = imageFile,
                        contentDescription = "Wardrobe item",
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .then(
                                if (isSelected) {
                                    Modifier.border(
                                        width = 3.dp,
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                } else {
                                    Modifier
                                }
                            )
                            .combinedClickable(
                                onClick = {
                                    if (selectedImages.isNotEmpty()) {
                                        if (isSelected) {
                                            selectedImages.remove(imageFile)
                                        } else {
                                            selectedImages.add(imageFile)
                                        }
                                    }
                                },
                                onLongClick = {
                                    if (isSelected) {
                                        selectedImages.remove(imageFile)
                                    } else {
                                        selectedImages.add(imageFile)
                                    }
                                }
                            ),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
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