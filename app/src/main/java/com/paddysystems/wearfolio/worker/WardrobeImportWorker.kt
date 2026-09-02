package com.paddysystems.wearfolio.worker

import android.content.Context
import android.net.Uri
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.paddysystems.wearfolio.data.storage.WardrobeCutoutService
import com.paddysystems.wearfolio.data.storage.WardrobeImportQueue
import com.paddysystems.wearfolio.data.storage.loadWardrobeItem
import com.paddysystems.wearfolio.data.storage.saveWardrobeItemFromImageFile
import com.paddysystems.wearfolio.ml.WardrobeAnalysisService
import kotlinx.coroutines.CancellationException
import java.io.File

class WardrobeImportWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val importId = inputData.getString(KEY_IMPORT_ID)
            ?: return Result.success()

        val profileId = inputData.getString(KEY_PROFILE_ID)
            ?: return Result.success()

        val existingItem = loadWardrobeItem(applicationContext, profileId, importId)

        if (existingItem != null) {
            runCatching {
                WardrobeCutoutService.ensureCutout(
                    context = applicationContext,
                    profileId = profileId,
                    item = existingItem
                )
            }

            WardrobeImportQueue.completeImport(applicationContext, profileId, importId)
            return Result.success()
        }

        val pendingImport = WardrobeImportQueue.findImport(
            context = applicationContext,
            profileId = profileId,
            importId = importId
        ) ?: return Result.success()

        val imageFile = File(pendingImport.imagePath)

        if (!imageFile.exists()) {
            WardrobeImportQueue.markFailed(
                context = applicationContext,
                profileId = profileId,
                importId = importId,
                errorMessage = "The original photo is no longer available."
            )
            return Result.success()
        }

        WardrobeImportQueue.markProcessing(applicationContext, profileId, importId)

        return try {
            val analysis = WardrobeAnalysisService.analyse(
                context = applicationContext,
                imageUri = Uri.fromFile(imageFile)
            )

            val clothingTypeId = analysis.clothingMatches
                .firstOrNull()
                ?.id
                ?: error("Could not identify the garment type.")

            val colours = analysis.colourMatches
                .firstOrNull()
                ?.let { listOf(it.id) }
                .orEmpty()

            if (colours.isEmpty()) {
                error("Could not identify a colour.")
            }

            val item = saveWardrobeItemFromImageFile(
                context = applicationContext,
                profileId = profileId,
                imageFile = imageFile,
                clothingTypeId = clothingTypeId,
                colours = colours,
                imageEmbedding = analysis.embedding,
                metadata = analysis.metadata
            ) ?: error("Could not save the analysed wardrobe item.")

            // A cut-out is derived data. Failure here must never discard a
            // successfully analysed and saved wardrobe item.
            runCatching {
                WardrobeCutoutService.ensureCutout(
                    context = applicationContext,
                    profileId = profileId,
                    item = item
                )
            }

            WardrobeImportQueue.completeImport(applicationContext, profileId, importId)
            Result.success()
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (exception: Exception) {
            WardrobeImportQueue.markFailed(
                context = applicationContext,
                profileId = profileId,
                importId = importId,
                errorMessage = exception.message ?: "Could not analyse this photo."
            )

            // The failed garment is represented in the queue UI. Returning
            // success is intentional so later garments in the chain continue.
            Result.success()
        }
    }

    companion object {
        const val KEY_IMPORT_ID = "wardrobe_import_id"
        const val KEY_PROFILE_ID = "wardrobe_import_profile_id"
    }
}
