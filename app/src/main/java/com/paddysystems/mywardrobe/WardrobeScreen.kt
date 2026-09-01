package com.paddysystems.mywardrobe
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
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

import com.paddysystems.mywardrobe.ui.components.WardrobeGrid
import com.paddysystems.mywardrobe.ui.components.WardrobeToolbar
import com.paddysystems.mywardrobe.ui.components.SelectionActions
import com.paddysystems.mywardrobe.ui.components.DeleteConfirmationDialog
import com.paddysystems.mywardrobe.ui.components.AddItemButton
import com.paddysystems.mywardrobe.ui.components.WardrobeHeader

@Composable
fun WardrobeScreen(
    modifier: Modifier = Modifier,
    onAddItemClick: () -> Unit = {},
    onItemClick: (File) -> Unit = {}
) {
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        WardrobeHeader(
            itemCount = images.size,
            selectedCount = selectedImages.size
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
                } else {
                    onItemClick(imageFile)
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
            DeleteConfirmationDialog(
                selectedCount = selectedImages.size,
                onDismiss = {
                    showDeleteConfirmation = false
                },
                onConfirm = {
                    val imagesToDelete = selectedImages.toList()

                    imagesToDelete.forEach { imageFile ->
                        if (deleteImage(imageFile)) {
                            images.remove(imageFile)
                        }
                    }

                    selectedImages.clear()
                    showDeleteConfirmation = false
                }
            )
        }

        AddItemButton(
            onClick = onAddItemClick
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WardrobePreview() {
    MyWardrobeTheme {
        WardrobeScreen()
    }
}