package com.paddysystems.wearfolio.data.storage

import android.content.Context
import com.paddysystems.wearfolio.data.model.WardrobeItem
import com.paddysystems.wearfolio.ml.IsNetBackgroundRemover
import java.io.File

object WardrobeCutoutService {

    fun ensureCutout(
        context: Context,
        profileId: String,
        item: WardrobeItem
    ): WardrobeItem {

        val existingPath =
            item.cutoutPath

        if (
            existingPath != null &&
            File(existingPath).exists()
        ) {
            return item
        }

        val cutoutFile =
            IsNetBackgroundRemover(
                context.applicationContext
            ).use { remover ->

                remover.createCutout(
                    imageFile =
                        File(
                            item.imagePath
                        ),

                    itemId =
                        item.id
                )
            }

        val updatedItem =
            updateWardrobeItemCutout(
                context =
                    context.applicationContext,

                profileId =
                    profileId,

                item =
                    item,

                cutoutPath =
                    cutoutFile.absolutePath
            )

        if (updatedItem == null) {
            cutoutFile.delete()

            return item
        }

        return updatedItem
    }
}