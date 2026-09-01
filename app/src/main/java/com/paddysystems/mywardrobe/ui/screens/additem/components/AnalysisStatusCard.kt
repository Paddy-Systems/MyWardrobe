package com.paddysystems.mywardrobe.ui.screens.additem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paddysystems.mywardrobe.ui.components.EditorialSecondaryButton

@Composable
fun AnalysisStatusCard(failed: Boolean, onRetry: () -> Unit) {
    Surface(Modifier.fillMaxWidth(), RoundedCornerShape(26.dp), color = MaterialTheme.colorScheme.surface) {
        Column(Modifier.padding(28.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            if (failed) {
                Text("We couldn’t read that photo", style = MaterialTheme.typography.titleLarge)
                Text("Try another image with the garment fully visible.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                EditorialSecondaryButton("Choose another", onRetry)
            } else {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.secondary)
                Text("Analysing colour and cut…", style = MaterialTheme.typography.titleLarge)
                Text("This usually takes a moment.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
