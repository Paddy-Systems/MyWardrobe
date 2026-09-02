package com.paddysystems.wearfolio.data.backup

import org.json.JSONArray
import org.json.JSONObject
import java.io.File

object WardrobeBackupValidator {
    private val safeIdPattern = Regex("^[A-Za-z0-9._-]+$")

    fun validate(root: File): BackupInspection {
        val errors = mutableListOf<String>()
        val warnings = mutableListOf<String>()
        val manifestFile = File(root, "manifest.json")
        val profilesFile = File(root, "profiles.json")
        val profilesDirectory = File(root, "profiles")

        if (!manifestFile.isFile) return invalid("This backup does not contain manifest.json.")
        if (!profilesFile.isFile) return invalid("This backup does not contain profiles.json.")
        if (!profilesDirectory.isDirectory) return invalid("This backup does not contain profile data.")

        val manifestJson = runCatching {
            JSONObject(manifestFile.readText())
        }.getOrElse {
            return invalid("The backup manifest could not be read.")
        }

        val formatVersion = manifestJson.optInt("formatVersion", -1)
        if (formatVersion != CURRENT_BACKUP_VERSION) {
            return invalid(
                "Backup format $formatVersion is not supported by this version of Wearfolio.",
                formatVersion
            )
        }

        val profilesJson = runCatching {
            JSONObject(profilesFile.readText())
        }.getOrElse {
            return invalid("The wardrobe profile list could not be read.", formatVersion)
        }

        val profilesArray = profilesJson.optJSONArray("profiles") ?: JSONArray()
        if (profilesArray.length() == 0) {
            errors += "The backup does not contain any wardrobes."
        }

        val profileIds = mutableListOf<String>()
        for (index in 0 until profilesArray.length()) {
            val profileJson = profilesArray.optJSONObject(index)
            if (profileJson == null) {
                errors += "A wardrobe profile entry is invalid."
                continue
            }

            val profileId = profileJson.optString("id").trim()
            val profileName = profileJson.optString("name").trim()

            if (!isSafeId(profileId)) {
                errors += "A wardrobe profile has an invalid identifier."
                continue
            }
            if (profileName.isBlank()) errors += "Wardrobe $profileId has no name."

            if (profileId in profileIds) {
                errors += "Wardrobe $profileId appears more than once."
            } else {
                profileIds.add(profileId)
            }
        }

        val activeProfileId = profilesJson.optString("activeProfileId")
            .takeIf { it.isNotBlank() }

        if (activeProfileId == null || activeProfileId !in profileIds) {
            errors += "The backup does not identify a valid active wardrobe."
        }

        var wardrobeItemCount = 0
        var outfitCount = 0

        profileIds.forEach profileLoop@ { profileId ->
            val profileDirectory = File(profilesDirectory, profileId)
            if (!profileDirectory.isDirectory) {
                errors += "Wardrobe $profileId is missing its data directory."
                return@profileLoop
            }

            val itemIds = validateWardrobeItems(
                profileDirectory,
                profileId,
                errors,
                warnings
            )
            wardrobeItemCount += itemIds.size

            outfitCount += validateOutfits(
                profileDirectory,
                profileId,
                itemIds,
                errors,
                warnings
            )
        }

        val expectedProfiles = manifestJson.optInt("profileCount", profileIds.size)
        val expectedItems = manifestJson.optInt("wardrobeItemCount", wardrobeItemCount)
        val expectedOutfits = manifestJson.optInt("outfitCount", outfitCount)

        if (expectedProfiles != profileIds.size) {
            warnings += "The manifest profile count differs from the archive contents."
        }
        if (expectedItems != wardrobeItemCount) {
            warnings += "The manifest wardrobe-item count differs from the archive contents."
        }
        if (expectedOutfits != outfitCount) {
            warnings += "The manifest saved-fit count differs from the archive contents."
        }

        val totalBytes = root.walkTopDown()
            .filter { it.isFile }
            .sumOf { it.length() }

        return BackupInspection(
            valid = errors.isEmpty(),
            formatVersion = formatVersion,
            createdAt = manifestJson.optLong("createdAt", 0L).takeIf { it > 0L },
            profileCount = profileIds.size,
            wardrobeItemCount = wardrobeItemCount,
            outfitCount = outfitCount,
            totalBytes = totalBytes,
            warnings = warnings,
            errorMessage = errors.takeIf { it.isNotEmpty() }?.joinToString("\n")
        )
    }

    private fun validateWardrobeItems(
        profileDirectory: File,
        profileId: String,
        errors: MutableList<String>,
        warnings: MutableList<String>
    ): Set<String> {
        val itemDirectory = File(profileDirectory, "wardrobe_items")
        if (!itemDirectory.exists()) return emptySet()
        if (!itemDirectory.isDirectory) {
            errors += "Wardrobe $profileId has an invalid wardrobe_items entry."
            return emptySet()
        }

        val imagesDirectory = File(profileDirectory, "wardrobe_images")
        val cutoutsDirectory = File(profileDirectory, "wardrobe_cutouts")
        val embeddingsDirectory = File(profileDirectory, "wardrobe_embeddings")
        val ids = linkedSetOf<String>()

        itemDirectory.listFiles()
            ?.filter { it.isFile && it.extension == "json" }
            ?.forEach itemLoop@ { itemFile ->
                val json = runCatching {
                    JSONObject(itemFile.readText())
                }.getOrElse {
                    errors += "Wardrobe $profileId contains unreadable item metadata: ${itemFile.name}."
                    return@itemLoop
                }

                val itemId = json.optString("id").trim()
                if (!isSafeId(itemId)) {
                    errors += "Wardrobe $profileId contains an invalid item identifier."
                    return@itemLoop
                }

                if (!ids.add(itemId)) {
                    errors += "Wardrobe $profileId contains duplicate item $itemId."
                }

                if (findFileForId(imagesDirectory, itemId) == null) {
                    errors += "Wardrobe $profileId item $itemId is missing its original image."
                }

                val embeddingFile = File(embeddingsDirectory, "$itemId.bin")
                if (
                    embeddingFile.exists() &&
                    (embeddingFile.length() == 0L || embeddingFile.length() % 4L != 0L)
                ) {
                    errors += "Wardrobe $profileId item $itemId has an invalid embedding."
                }

                if (!json.isNull("cutoutPath") && findFileForId(cutoutsDirectory, itemId) == null) {
                    warnings += "Wardrobe $profileId item $itemId is missing its cut-out; the original image will still be usable."
                }
            }

        return ids
    }

    private fun validateOutfits(
        profileDirectory: File,
        profileId: String,
        validItemIds: Set<String>,
        errors: MutableList<String>,
        warnings: MutableList<String>
    ): Int {
        val outfitsDirectory = File(profileDirectory, "outfits")
        if (!outfitsDirectory.exists()) return 0
        if (!outfitsDirectory.isDirectory) {
            errors += "Wardrobe $profileId has an invalid saved-fits entry."
            return 0
        }

        var count = 0

        outfitsDirectory.listFiles()
            ?.filter { it.isFile && it.extension == "json" }
            ?.forEach outfitLoop@ { outfitFile ->
                val json = runCatching {
                    JSONObject(outfitFile.readText())
                }.getOrElse {
                    errors += "Wardrobe $profileId contains an unreadable saved fit: ${outfitFile.name}."
                    return@outfitLoop
                }

                val outfitId = json.optString("id").trim()
                if (outfitId.isBlank()) {
                    errors += "Wardrobe $profileId contains a saved fit without an identifier."
                    return@outfitLoop
                }

                count++

                val missing = referencedItemIds(json).filter { it !in validItemIds }
                if (missing.isNotEmpty()) {
                    warnings += "Saved fit ${json.optString("name", outfitId)} in wardrobe $profileId references ${missing.size} missing piece(s)."
                }
            }

        return count
    }

    private fun referencedItemIds(outfitJson: JSONObject): Set<String> {
        val ids = linkedSetOf<String>()

        fun addSlot(slot: JSONObject?) {
            slot?.optString("itemId")
                ?.takeIf { it.isNotBlank() }
                ?.let(ids::add)
        }

        outfitJson.optJSONArray("layers")?.let { layers ->
            for (index in 0 until layers.length()) {
                val layer = layers.optJSONObject(index) ?: continue
                addSlot(layer.optJSONObject("top"))
                addSlot(layer.optJSONObject("bottom"))
                addSlot(layer.optJSONObject("fullLength"))
            }
        }

        addSlot(outfitJson.optJSONObject("shoes"))
        addSlot(outfitJson.optJSONObject("bag"))

        outfitJson.optJSONArray("accessories")?.let { accessories ->
            for (index in 0 until accessories.length()) {
                addSlot(accessories.optJSONObject(index))
            }
        }

        return ids
    }

    private fun isSafeId(id: String): Boolean {
        return id.isNotBlank() &&
            safeIdPattern.matches(id) &&
            id != "." &&
            id != ".." &&
            !id.contains("..")
    }

    private fun findFileForId(directory: File, id: String): File? {
        if (!directory.isDirectory) return null
        return directory.listFiles()
            ?.firstOrNull { it.isFile && it.nameWithoutExtension == id }
    }

    private fun invalid(
        message: String,
        formatVersion: Int? = null
    ): BackupInspection {
        return BackupInspection(
            valid = false,
            formatVersion = formatVersion,
            errorMessage = message
        )
    }
}
