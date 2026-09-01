package com.paddysystems.mywardrobe.ui.screens.additem.components

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
            modifier = Modifier.height(8.dp)
        )

        Text(
            "Pinch to zoom · drag to position"
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        CropEditor(
            imageUri = imageUri,
            cropType = cropType
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