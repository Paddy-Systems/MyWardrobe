package com.paddysystems.wearfolio.data.model

enum class WardrobeImportStatus {
    QUEUED,
    PROCESSING,
    FAILED
}

data class WardrobeImport(
    val id: String,
    val imagePath: String,
    val createdAt: Long,
    val status: WardrobeImportStatus = WardrobeImportStatus.QUEUED,
    val errorMessage: String? = null
)
