package com.paddysystems.mywardrobe.ui.navigation

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MyWardrobeBottomBar(
    onCreateOutfit: () -> Unit,
    onAddItem: () -> Unit,
    onViewOutfits: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Button(
            modifier =
                Modifier.weight(1f),

            onClick =
                onCreateOutfit
        ) {
            Text("Create Outfit")
        }

        FilledIconButton(
            modifier =
                Modifier.padding(
                    horizontal = 12.dp
                ),

            onClick =
                onAddItem
        ) {
            /*
             * Temporary camera representation.
             * We'll replace with the actual
             * Material camera icon next.
             */
            Text("📷")
        }

        Button(
            modifier =
                Modifier.weight(1f),

            onClick =
                onViewOutfits
        ) {
            Text("View Outfits")
        }
    }
}