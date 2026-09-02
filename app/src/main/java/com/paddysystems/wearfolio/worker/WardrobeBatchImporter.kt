package com.paddysystems.wearfolio.worker

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf

object WardrobeBatchImporter {
    private const val UNIQUE_WORK_NAME = "wardrobe-batch-import"

    fun enqueue(
        context: Context,
        profileId: String,
        importIds: List<String>
    ) {
        if (importIds.isEmpty()) {
            return
        }

        val requests = importIds.map { importId ->
            OneTimeWorkRequestBuilder<WardrobeImportWorker>()
                .setInputData(
                    workDataOf(
                        WardrobeImportWorker.KEY_IMPORT_ID to importId,
                        WardrobeImportWorker.KEY_PROFILE_ID to profileId
                    )
                )
                .addTag("wardrobe-import-$importId")
                .build()
        }

        val workManager = WorkManager.getInstance(context.applicationContext)

        var continuation = workManager.beginUniqueWork(
            UNIQUE_WORK_NAME,
            ExistingWorkPolicy.APPEND_OR_REPLACE,
            requests.first()
        )

        requests.drop(1).forEach { request ->
            continuation = continuation.then(request)
        }

        continuation.enqueue()
    }

    fun retry(
        context: Context,
        profileId: String,
        importId: String
    ) {
        enqueue(context, profileId, listOf(importId))
    }
}
