package com.paddysystems.wearfolio.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paddysystems.wearfolio.data.model.WardrobeImport
import com.paddysystems.wearfolio.data.model.WardrobeImportStatus

@Composable
fun WardrobeImportStatus(
    imports: List<WardrobeImport>,
    modifier: Modifier = Modifier
) {
    if (imports.isEmpty()) {
        return
    }

    val processing = imports.count { it.status == WardrobeImportStatus.PROCESSING }
    val queued = imports.count { it.status == WardrobeImportStatus.QUEUED }
    val failed = imports.count { it.status == WardrobeImportStatus.FAILED }

    val summary = buildList {
        if (processing > 0) {
            add("$processing analysing")
        }
        if (queued > 0) {
            add("$queued waiting")
        }
        if (failed > 0) {
            add("$failed ${if (failed == 1) "needs" else "need"} attention")
        }
    }.joinToString("  ·  ")

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "ADDING TO YOUR WARDROBE",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(5.dp))

            Text(
                text = summary,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(Modifier.height(3.dp))

            Text(
                text = "We process each piece one at a time. You can keep using the app while we work.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
