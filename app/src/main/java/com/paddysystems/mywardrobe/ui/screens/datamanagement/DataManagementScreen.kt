package com.paddysystems.mywardrobe.ui.screens.datamanagement

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.CreateDocument
import androidx.activity.result.contract.ActivityResultContracts.OpenDocument
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.SettingsBackupRestore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.paddysystems.mywardrobe.data.backup.BackupInspection
import com.paddysystems.mywardrobe.data.backup.BackupProgress
import com.paddysystems.mywardrobe.data.backup.WardrobeBackupService
import com.paddysystems.mywardrobe.data.backup.WardrobeDataSummary
import com.paddysystems.mywardrobe.data.model.Profile
import com.paddysystems.mywardrobe.ui.components.EditorialPageHeader
import com.paddysystems.mywardrobe.ui.components.EditorialPrimaryButton
import com.paddysystems.mywardrobe.ui.components.EditorialSecondaryButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun DataManagementScreen(
    onBack: () -> Unit,
    onRestored: (Profile) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var summary by remember { mutableStateOf<WardrobeDataSummary?>(null) }
    var inspection by remember { mutableStateOf<BackupInspection?>(null) }
    var isBusy by remember { mutableStateOf(false) }
    var busyLabel by remember { mutableStateOf<String?>(null) }
    var progress by remember { mutableStateOf<BackupProgress?>(null) }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    suspend fun refreshSummary() {
        summary = withContext(Dispatchers.IO) {
            WardrobeBackupService.calculateSummary(context.applicationContext)
        }
    }

    LaunchedEffect(Unit) {
        refreshSummary()
    }

    val exportLauncher = rememberLauncherForActivityResult(
        contract = CreateDocument("application/zip")
    ) { uri ->
        if (uri != null) {
            scope.launch {
                isBusy = true
                busyLabel = "Creating backup"
                statusMessage = null
                errorMessage = null
                progress = null

                val result = withContext(Dispatchers.IO) {
                    WardrobeBackupService.exportBackup(
                        context = context.applicationContext,
                        destination = uri,
                        onProgress = { update ->
                            scope.launch { progress = update }
                        }
                    )
                }

                isBusy = false
                busyLabel = null
                progress = null

                result.fold(
                    onSuccess = { manifest ->
                        statusMessage =
                            "Backup created with ${manifest.profileCount} ${wardrobeWord(manifest.profileCount)}, " +
                                "${manifest.wardrobeItemCount} ${pieceWord(manifest.wardrobeItemCount)} and " +
                                "${manifest.outfitCount} saved ${fitWord(manifest.outfitCount)}."
                    },
                    onFailure = { exception ->
                        errorMessage = exception.message ?: "Could not create the backup."
                    }
                )
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = OpenDocument()
    ) { uri ->
        if (uri != null) {
            scope.launch {
                isBusy = true
                busyLabel = "Checking backup"
                statusMessage = null
                errorMessage = null
                inspection = null

                val result = withContext(Dispatchers.IO) {
                    WardrobeBackupService.inspectBackup(
                        context.applicationContext,
                        uri
                    )
                }

                isBusy = false
                busyLabel = null

                if (result.valid) {
                    inspection = result
                } else {
                    errorMessage = result.errorMessage ?: "This is not a valid Wearfolio backup."
                }
            }
        }
    }

    val currentSummary = summary
    val hasPendingImports = (currentSummary?.pendingImports ?: 0) > 0
    val actionsEnabled = currentSummary != null && !hasPendingImports && !isBusy

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 22.dp)
    ) {
        EditorialPageHeader(
            eyebrow = "Your data",
            title = "Backup & restore",
            subtitle = "Keep every wardrobe, original photo and saved fit safe, or move them to another device.",
            navigationIcon = Icons.AutoMirrored.Outlined.ArrowBack,
            onNavigate = onBack
        )

        Spacer(Modifier.height(22.dp))
        DataSummaryCard(currentSummary)

        if (hasPendingImports) {
            Spacer(Modifier.height(14.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        "BACKUP PAUSED",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        "${currentSummary?.pendingImports ?: 0} ${pieceWord(currentSummary?.pendingImports ?: 0)} still being added",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        "Let every batch import finish, retry it, or remove it before exporting or restoring data.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        if (isBusy) {
            Spacer(Modifier.height(18.dp))
            WorkingCard(busyLabel ?: "Working", progress)
        }

        statusMessage?.let {
            Spacer(Modifier.height(14.dp))
            MessageCard(it, isError = false)
        }

        errorMessage?.let {
            Spacer(Modifier.height(14.dp))
            MessageCard(it, isError = true)
        }

        Spacer(Modifier.height(26.dp))
        SectionLabel("BACKUP")
        Text(
            "Create one portable .mwbackup file containing all wardrobes, item metadata, original photos, cut-outs, embeddings and saved fits.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(14.dp))
        EditorialPrimaryButton(
            text = "Export all wardrobes",
            icon = Icons.Outlined.Archive,
            enabled = actionsEnabled,
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                exportLauncher.launch(WardrobeBackupService.suggestedFileName())
            }
        )

        Spacer(Modifier.height(28.dp))
        HorizontalDivider()
        Spacer(Modifier.height(28.dp))

        SectionLabel("RESTORE")
        Text(
            "Inspect a Wearfolio backup first, then explicitly replace the wardrobes stored on this device. Invalid archives never touch your current data.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(14.dp))
        EditorialSecondaryButton(
            text = "Import backup",
            icon = Icons.Outlined.SettingsBackupRestore,
            enabled = actionsEnabled,
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                importLauncher.launch(
                    arrayOf(
                        "application/zip",
                        "application/octet-stream",
                        "application/x-zip-compressed"
                    )
                )
            }
        )
        Spacer(Modifier.height(28.dp))
    }

    inspection?.let { candidate ->
        RestoreConfirmationDialog(
            inspection = candidate,
            currentSummary = currentSummary,
            onDismiss = {
                inspection = null
                scope.launch(Dispatchers.IO) {
                    WardrobeBackupService.discardPreparedRestore(candidate)
                }
            },
            onConfirm = {
                inspection = null
                isBusy = true
                busyLabel = "Restoring wardrobes"
                statusMessage = null
                errorMessage = null

                scope.launch {
                    val result = withContext(Dispatchers.IO) {
                        WardrobeBackupService.restorePreparedBackup(
                            context.applicationContext,
                            candidate
                        )
                    }

                    isBusy = false
                    busyLabel = null

                    result.fold(
                        onSuccess = onRestored,
                        onFailure = { exception ->
                            errorMessage = exception.message ?: "Could not restore this backup."
                            refreshSummary()
                        }
                    )
                }
            }
        )
    }
}

@Composable
private fun DataSummaryCard(summary: WardrobeDataSummary?) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SectionLabel("ALL WARDROBES")

            if (summary == null) {
                CircularProgressIndicator()
            } else {
                Text(
                    "${summary.profileCount} ${wardrobeWord(summary.profileCount)}",
                    style = MaterialTheme.typography.titleLarge
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    SummaryMetric(
                        summary.wardrobeItemCount.toString(),
                        pieceWord(summary.wardrobeItemCount)
                    )
                    SummaryMetric(
                        summary.outfitCount.toString(),
                        "saved ${fitWord(summary.outfitCount)}"
                    )
                    SummaryMetric(formatBytes(summary.totalBytes), "stored")
                }
            }
        }
    }
}

@Composable
private fun SummaryMetric(value: String, label: String) {
    Column {
        Text(value, style = MaterialTheme.typography.titleMedium)
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun WorkingCard(label: String, progress: BackupProgress?) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            CircularProgressIndicator()
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(label, style = MaterialTheme.typography.titleMedium)
                progress?.let {
                    Text(progressText(it), style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
private fun MessageCard(message: String, isError: Boolean) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = if (isError) {
            MaterialTheme.colorScheme.errorContainer
        } else {
            MaterialTheme.colorScheme.surface
        }
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = if (isError) {
                MaterialTheme.colorScheme.onErrorContainer
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        )
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun RestoreConfirmationDialog(
    inspection: BackupInspection,
    currentSummary: WardrobeDataSummary?,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val backupDate = inspection.createdAt?.let {
        DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(Date(it))
    } ?: "Unknown date"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Restore backup?") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Backup created $backupDate")
                Text(
                    "${inspection.profileCount} ${wardrobeWord(inspection.profileCount)} · " +
                        "${inspection.wardrobeItemCount} ${pieceWord(inspection.wardrobeItemCount)} · " +
                        "${inspection.outfitCount} saved ${fitWord(inspection.outfitCount)}"
                )

                currentSummary?.let {
                    Text(
                        "This device currently has ${it.profileCount} ${wardrobeWord(it.profileCount)}, " +
                            "${it.wardrobeItemCount} ${pieceWord(it.wardrobeItemCount)} and " +
                            "${it.outfitCount} saved ${fitWord(it.outfitCount)}."
                    )
                }

                if (inspection.warnings.isNotEmpty()) {
                    Text(
                        inspection.warnings.take(4).joinToString("\n"),
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                Text(
                    "Restoring replaces every wardrobe currently stored on this device. " +
                        "The existing data is kept as a rollback copy until the restore succeeds."
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text("Restore") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

private fun progressText(progress: BackupProgress): String {
    val percent = if (progress.totalBytes <= 0L) {
        0
    } else {
        (
            progress.bytesCompleted.toDouble() /
                progress.totalBytes.toDouble() *
                100.0
            ).roundToInt().coerceIn(0, 100)
    }

    return "${progress.filesCompleted} / ${progress.totalFiles} files · $percent%"
}

private fun formatBytes(bytes: Long): String {
    if (bytes < 1024L) return "$bytes B"

    val units = listOf("KB", "MB", "GB", "TB")
    var value = bytes.toDouble() / 1024.0
    var unitIndex = 0

    while (value >= 1024.0 && unitIndex < units.lastIndex) {
        value /= 1024.0
        unitIndex++
    }

    return if (value >= 100) {
        "${value.roundToInt()} ${units[unitIndex]}"
    } else {
        String.format(Locale.UK, "%.1f %s", value, units[unitIndex])
    }
}

private fun wardrobeWord(count: Int) = if (count == 1) "wardrobe" else "wardrobes"
private fun pieceWord(count: Int) = if (count == 1) "piece" else "pieces"
private fun fitWord(count: Int) = if (count == 1) "fit" else "fits"
