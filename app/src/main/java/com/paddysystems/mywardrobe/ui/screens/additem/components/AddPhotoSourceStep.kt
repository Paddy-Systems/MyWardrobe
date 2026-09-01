package com.paddysystems.mywardrobe.ui.screens.additem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paddysystems.mywardrobe.ui.components.EditorialPrimaryButton
import com.paddysystems.mywardrobe.ui.components.EditorialSecondaryButton

@Composable
fun AddPhotoSourceStep(onChoosePhoto: () -> Unit, onTakePhoto: () -> Unit) {
    Surface(Modifier.fillMaxWidth(), RoundedCornerShape(26.dp), color = MaterialTheme.colorScheme.surface) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(Icons.Outlined.AutoAwesome, null, tint = MaterialTheme.colorScheme.secondary)
            Text("Start with a photo", style = MaterialTheme.typography.titleLarge)
            Text("A simple front-facing shot works best.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
    Spacer(Modifier.height(18.dp))
    EditorialPrimaryButton("Choose from photos", onChoosePhoto, Modifier.fillMaxWidth(), icon = Icons.Outlined.PhotoLibrary)
    Spacer(Modifier.height(12.dp))
    EditorialSecondaryButton("Take a photo", onTakePhoto, Modifier.fillMaxWidth(), icon = Icons.Outlined.PhotoCamera)
}
