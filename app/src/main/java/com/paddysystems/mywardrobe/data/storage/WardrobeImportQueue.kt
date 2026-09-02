package com.paddysystems.mywardrobe.data.storage

import android.content.Context
import android.net.Uri
import com.paddysystems.mywardrobe.data.model.WardrobeImport
import com.paddysystems.mywardrobe.data.model.WardrobeImportStatus
import com.paddysystems.mywardrobe.saveImage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject
import java.io.File
import java.util.UUID

object WardrobeImportQueue {
    private const val DIRECTORY_NAME = "wardrobe_import_queue"

    private val importsFlow = MutableStateFlow<List<WardrobeImport>>(emptyList())

    @Synchronized
    fun observe(context: Context): StateFlow<List<WardrobeImport>> {
        refresh(context.applicationContext)
        return importsFlow.asStateFlow()
    }

    @Synchronized
    fun loadImports(context: Context): List<WardrobeImport> {
        val directory = queueDirectory(context)

        if (!directory.exists()) {
            return emptyList()
        }

        return directory
            .listFiles()
            ?.filter { it.isFile && it.extension == "json" }
            ?.mapNotNull { file ->
                runCatching {
                    importFromJson(JSONObject(file.readText()))
                }.getOrNull()
            }
            ?.sortedBy { it.createdAt }
            ?: emptyList()
    }

    @Synchronized
    fun findImport(
        context: Context,
        importId: String
    ): WardrobeImport? {
        val file = queueFile(context, importId)

        if (!file.exists()) {
            return null
        }

        return runCatching {
            importFromJson(JSONObject(file.readText()))
        }.getOrNull()
    }

    @Synchronized
    fun stageImages(
        context: Context,
        imageUris: List<Uri>
    ): List<WardrobeImport> {
        val applicationContext = context.applicationContext
        val startedAt = System.currentTimeMillis()

        val staged = imageUris.mapIndexedNotNull { index, uri ->
            val id = UUID.randomUUID().toString()
            val imageFile = saveImage(
                context = applicationContext,
                uri = uri,
                imageId = id
            ) ?: return@mapIndexedNotNull null

            val item = WardrobeImport(
                id = id,
                imagePath = imageFile.absolutePath,
                createdAt = startedAt + index,
                status = WardrobeImportStatus.QUEUED
            )

            if (writeImport(applicationContext, item)) {
                item
            } else {
                imageFile.delete()
                null
            }
        }

        refresh(applicationContext)
        return staged
    }

    @Synchronized
    fun markQueued(
        context: Context,
        importId: String
    ): WardrobeImport? {
        return updateStatus(
            context = context,
            importId = importId,
            status = WardrobeImportStatus.QUEUED,
            errorMessage = null
        )
    }

    @Synchronized
    fun markProcessing(
        context: Context,
        importId: String
    ): WardrobeImport? {
        return updateStatus(
            context = context,
            importId = importId,
            status = WardrobeImportStatus.PROCESSING,
            errorMessage = null
        )
    }

    @Synchronized
    fun markFailed(
        context: Context,
        importId: String,
        errorMessage: String?
    ): WardrobeImport? {
        return updateStatus(
            context = context,
            importId = importId,
            status = WardrobeImportStatus.FAILED,
            errorMessage = errorMessage?.take(180)
        )
    }

    @Synchronized
    fun completeImport(
        context: Context,
        importId: String
    ) {
        queueFile(context, importId).delete()
        refresh(context.applicationContext)
    }

    @Synchronized
    fun discardImport(
        context: Context,
        importId: String
    ): Boolean {
        val applicationContext = context.applicationContext
        val import = findImport(applicationContext, importId)

        if (import == null) {
            refresh(applicationContext)
            return true
        }

        val queueDeleted = queueFile(applicationContext, importId).delete()

        val finishedItemExists =
            loadWardrobeItem(applicationContext, importId) != null

        val imageDeleted = if (finishedItemExists) {
            true
        } else {
            val imageFile = File(import.imagePath)
            !imageFile.exists() || imageFile.delete()
        }

        refresh(applicationContext)
        return queueDeleted && imageDeleted
    }

    @Synchronized
    fun refresh(context: Context) {
        importsFlow.value = loadImports(context.applicationContext)
    }

    private fun updateStatus(
        context: Context,
        importId: String,
        status: WardrobeImportStatus,
        errorMessage: String?
    ): WardrobeImport? {
        val applicationContext = context.applicationContext
        val existing = findImport(applicationContext, importId) ?: return null
        val updated = existing.copy(
            status = status,
            errorMessage = errorMessage
        )

        if (!writeImport(applicationContext, updated)) {
            return null
        }

        refresh(applicationContext)
        return updated
    }

    private fun writeImport(
        context: Context,
        item: WardrobeImport
    ): Boolean {
        val directory = queueDirectory(context)
        directory.mkdirs()

        return runCatching {
            queueFile(context, item.id).writeText(
                JSONObject()
                    .put("id", item.id)
                    .put("imagePath", item.imagePath)
                    .put("createdAt", item.createdAt)
                    .put("status", item.status.name)
                    .put("errorMessage", item.errorMessage ?: JSONObject.NULL)
                    .toString()
            )
            true
        }.getOrDefault(false)
    }

    private fun importFromJson(json: JSONObject): WardrobeImport {
        val status = runCatching {
            WardrobeImportStatus.valueOf(
                json.optString("status", WardrobeImportStatus.QUEUED.name)
            )
        }.getOrDefault(WardrobeImportStatus.QUEUED)

        return WardrobeImport(
            id = json.getString("id"),
            imagePath = json.getString("imagePath"),
            createdAt = json.optLong("createdAt", 0L),
            status = status,
            errorMessage = if (json.isNull("errorMessage")) {
                null
            } else {
                json.optString("errorMessage").takeIf { it.isNotBlank() }
            }
        )
    }

    private fun queueDirectory(context: Context): File {
        return File(context.filesDir, DIRECTORY_NAME)
    }

    private fun queueFile(
        context: Context,
        importId: String
    ): File {
        return File(queueDirectory(context), "$importId.json")
    }
}
