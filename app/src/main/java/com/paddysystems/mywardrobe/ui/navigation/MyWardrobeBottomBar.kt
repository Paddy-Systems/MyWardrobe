package com.paddysystems.mywardrobe.ui.navigation

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MyWardrobeBottomBar(
    showWardrobeOnLeft: Boolean,
    showWardrobeOnRight: Boolean,
    onCreateOutfit: () -> Unit,
    onAddItem: () -> Unit,
    onViewOutfits: () -> Unit,
    onWardrobe: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(88.dp)
            .padding(
                horizontal = 8.dp,
                vertical = 8.dp
            ),
        verticalAlignment =
            Alignment.CenterVertically
    ) {
        Button(
            modifier = Modifier
                .weight(1f)
                .height(48.dp),

            onClick = {
                if (showWardrobeOnLeft) {
                    onWardrobe()
                } else {
                    onCreateOutfit()
                }
            }
        ) {
            Text(
                if (showWardrobeOnLeft) {
                    "Wardrobe"
                } else {
                    "Create Outfit"
                }
            )
        }

        FilledIconButton(
            modifier = Modifier
                .padding(
                    horizontal = 12.dp
                )
                .size(72.dp)
                .offset(
                    y = (-6).dp
                ),

            onClick =
                onAddItem
        ) {
            Text(
                text = "📷",
                fontSize = 28.sp
            )
        }

        Button(
            modifier = Modifier
                .weight(1f)
                .height(48.dp),

            onClick = {
                if (showWardrobeOnRight) {
                    onWardrobe()
                } else {
                    onViewOutfits()
                }
            }
        ) {
            Text(
                if (showWardrobeOnRight) {
                    "Wardrobe"
                } else {
                    "View Outfits"
                }
            )
        }
    }
}