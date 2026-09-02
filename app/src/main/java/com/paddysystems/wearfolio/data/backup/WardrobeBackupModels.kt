package com.paddysystems.wearfolio.data.backup

const val CURRENT_BACKUP_VERSION = 1

data class WardrobeBackupManifest(
    val formatVersion: Int,
    val createdAt: Long,
    val appVersion: String,
    val profileCount: Int,
    val wardrobeItemCount: Int,
    val outfitCount: Int
)

data class WardrobeDataSummary(
    val profileCount: Int,
    val wardrobeItemCount: Int,
    val outfitCount: Int,
    val totalBytes: Long,
    val pendingImports: Int
)

data class BackupProgress(
    val filesCompleted: Int,
    val totalFiles: Int,
    val bytesCompleted: Long,
    val totalBytes: Long
)

data class BackupInspection(
    val valid: Boolean,
    val formatVersion: Int? = null,
    val createdAt: Long? = null,
    val profileCount: Int = 0,
    val wardrobeItemCount: Int = 0,
    val outfitCount: Int = 0,
    val totalBytes: Long = 0L,
    val warnings: List<String> = emptyList(),
    val errorMessage: String? = null,
    val stagingPath: String? = null
)
