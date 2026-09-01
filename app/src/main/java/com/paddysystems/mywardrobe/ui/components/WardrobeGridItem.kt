package com.paddysystems.mywardrobe.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.paddysystems.mywardrobe.data.model.WardrobeItem
import java.io.File

@Composable
internal fun WardrobeGridItem(item: WardrobeItem, isSelected: Boolean, onClick: () -> Unit, onLongClick: () -> Unit) {
    val image = item.cutoutPath?.let(::File)?.takeIf(File::exists) ?: File(item.imagePath)
    val selectionBorder = if (isSelected) Modifier.border(3.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(18.dp)) else Modifier
    Surface(
        modifier = Modifier.fillMaxWidth().aspectRatio(1f).then(selectionBorder).combinedClickable(onClick = onClick, onLongClick = onLongClick),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = if (isSelected) 0.dp else 1.dp
    ) {
        Box {
            AsyncImage(image, "Wardrobe item", Modifier.fillMaxWidth().padding(10.dp), contentScale = ContentScale.Fit)
            if (isSelected) {
                Surface(Modifier.align(Alignment.TopEnd).padding(8.dp), RoundedCornerShape(50), color = MaterialTheme.colorScheme.primary) {
                    Icon(Icons.Rounded.Check, null, Modifier.padding(4.dp), tint = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
    }
}
