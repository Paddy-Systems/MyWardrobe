package com.paddysystems.mywardrobe.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.paddysystems.mywardrobe.data.model.WardrobeImport
import com.paddysystems.mywardrobe.data.model.WardrobeImportStatus
import com.paddysystems.mywardrobe.data.model.WardrobeItem
import java.io.File

@Composable
fun WardrobeGrid(
    items: List<WardrobeItem>,
    selectedItems: List<WardrobeItem>,
    onItemClick: (WardrobeItem) -> Unit,
    onItemLongClick: (WardrobeItem) -> Unit,
    modifier: Modifier = Modifier,
    emptyMessage: String = "No items yet",
    pendingImports: List<WardrobeImport> = emptyList(),
    onImportRetry: (WardrobeImport) -> Unit = {},
    onImportRemove: (WardrobeImport) -> Unit = {}
) {
    if (items.isEmpty() && pendingImports.isEmpty()) {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(emptyMessage)
        }
        return
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(
            items = pendingImports,
            key = { item -> "import-${item.id}" }
        ) { item ->
            WardrobeImportGridItem(
                item = item,
                onRetry = { onImportRetry(item) },
                onRemove = { onImportRemove(item) }
            )
        }

        items(
            items = items,
            key = { item -> item.id }
        ) { item ->
            WardrobeGridItem(
                item = item,
                isSelected = selectedItems.contains(item),
                onClick = { onItemClick(item) },
                onLongClick = { onItemLongClick(item) }
            )
        }
    }
}

@Composable
private fun WardrobeImportGridItem(
    item: WardrobeImport,
    onRetry: () -> Unit,
    onRemove: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp
    ) {
        Box {
            AsyncImage(
                model = File(item.imagePath),
                contentDescription = "Incoming wardrobe item",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .alpha(if (item.status == WardrobeImportStatus.FAILED) 0.6f else 0.72f),
                contentScale = ContentScale.Fit
            )

            when (item.status) {
                WardrobeImportStatus.PROCESSING -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(30.dp),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 3.dp
                    )

                    ImportStatusLabel(
                        text = "ANALYSING",
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                }

                WardrobeImportStatus.QUEUED -> {
                    ImportStatusLabel(
                        text = "QUEUED",
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                }

                WardrobeImportStatus.FAILED -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "NEEDS ATTENTION",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            TextButton(onClick = onRetry) {
                                Text("Retry", style = MaterialTheme.typography.labelSmall)
                            }
                            TextButton(onClick = onRemove) {
                                Text("Remove", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ImportStatusLabel(
    text: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.padding(7.dp),
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun WardrobeGridItem(
    item: WardrobeItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val imageFile = item.cutoutPath
        ?.let(::File)
        ?.takeIf(File::exists)
        ?: File(item.imagePath)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .then(
                if (isSelected) {
                    Modifier.border(
                        width = 3.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(18.dp)
                    )
                } else {
                    Modifier
                }
            )
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = if (isSelected) 0.dp else 1.dp
    ) {
        Box {
            AsyncImage(
                model = imageFile,
                contentDescription = "Wardrobe item",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                contentScale = ContentScale.Fit
            )

            if (isSelected) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp),
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        Icons.Rounded.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(4.dp)
                    )
                }
            }
        }
    }
}
