package com.paddysystems.mywardrobe.ui.screens.additem.components

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.paddysystems.mywardrobe.data.model.CropType

@Composable
fun CropStep(
    imageUri: Uri,
    cropType: CropType,
    onBack: () -> Unit,
    onContinue: () -> Unit
) {
    Column {
        Text("Crop item")

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = when (cropType) {
                CropType.FULL_BODY -> "Full body · 1:2"
                CropType.HALF_PIECE -> "Half piece · 1:1"
            }
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        AsyncImage(
            model = imageUri,
            contentDescription = "Crop preview",
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(cropType.aspectRatio)
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onContinue
        ) {
            Text("Continue")
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = onBack
        ) {
            Text("Back")
        }
    }
}