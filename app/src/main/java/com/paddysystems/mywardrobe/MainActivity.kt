package com.paddysystems.mywardrobe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.paddysystems.mywardrobe.ui.theme.MyWardrobeTheme

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia

import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember

import coil3.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width

import android.content.Context
import androidx.compose.material3.ButtonColors
import androidx.compose.ui.platform.LocalContext
import java.io.File
import java.util.UUID

import androidx.compose.material3.MaterialTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyWardrobeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    WardrobeScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun WardrobeScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
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
            text = "My Wardrobe · ${images.size} items",
            style = MaterialTheme.typography.headlineLarge
        )

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
//                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    //
                }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Search")
                }
            }
            Button(
//                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    //
                }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Filter")
                }
            }
            Button(
//                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    //
                }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Sort")
                }
            }
        }

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
                    AsyncImage(
                        model = imageFile,
                        contentDescription = "Wardrobe item",
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }
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

fun saveImage(context: Context, uri: Uri): File? {
    val imageDirectory = File(
        context.filesDir,
        "wardrobe_images"
    )

    imageDirectory.mkdirs()

    val imageFile = File(
        imageDirectory,
        "${UUID.randomUUID()}.jpg"
    )

    return try {
        context.contentResolver
            .openInputStream(uri)
            ?.use { input ->
                imageFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

        imageFile
    } catch (exception: Exception) {
        null
    }
}

fun loadImages(context: Context): List<File> {
    val imageDirectory = File(
        context.filesDir,
        "wardrobe_images"
    )

    if (!imageDirectory.exists()) {
        return emptyList()
    }

    return imageDirectory
        .listFiles()
        ?.filter { it.isFile }
        ?.sortedByDescending { it.lastModified() }
        ?: emptyList()
}

@Preview(showBackground = true)
@Composable
fun WardrobePreview() {
    MyWardrobeTheme {
        WardrobeScreen()
    }
}