package com.paddysystems.wearfolio.data.backup

import android.content.Context
import android.net.Uri
import com.paddysystems.wearfolio.data.model.Profile
import com.paddysystems.wearfolio.data.storage.ProfileStorage
import com.paddysystems.wearfolio.data.storage.WardrobeImportQueue
import com.paddysystems.wearfolio.data.storage.loadOutfits
import com.paddysystems.wearfolio.data.storage.loadWardrobeItems
import com.paddysystems.wearfolio.data.storage.syncWardrobeOutfitIds
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

object WardrobeBackupService {
    private const val MANIFEST_FILE = "manifest.json"
    private const val PROFILES_STATE_FILE = "profiles.json"
    private const val PROFILES_DIRECTORY = "profiles"
    private const val MAX_ARCHIVE_ENTRIES = 20_000
    private const val MAX_EXTRACTED_BYTES = 20L * 1024L * 1024L * 1024L

    private val profileDataDirectories = listOf(
        "wardrobe_items",
        "wardrobe_images",
        "wardrobe_cutouts",
        "wardrobe_embeddings",
        "outfits",
        "outfit_previews"
    )

    fun calculateSummary(context: Context): WardrobeDataSummary {
        val appContext = context.applicationContext
        val profiles = ProfileStorage.loadProfiles(appContext)

        return WardrobeDataSummary(
            profileCount = profiles.size,
            wardrobeItemCount = profiles.sumOf {
                loadWardrobeItems(appContext, it.id).size
            },
            outfitCount = profiles.sumOf {
                loadOutfits(appContext, it.id).size
            },
            totalBytes = backupSourceFiles(appContext, profiles).sumOf { it.second.length() },
            pendingImports = profiles.sumOf {
                WardrobeImportQueue.loadImports(appContext, it.id).size
            }
        )
    }

    fun suggestedFileName(): String {
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.UK).format(Date())
        return "Wearfolio-$date.wearfolio"
    }

    fun exportBackup(
        context: Context,
        destination: Uri,
        onProgress: (BackupProgress) -> Unit = {}
    ): Result<WardrobeBackupManifest> = runCatching {
        val appContext = context.applicationContext
        val summary = calculateSummary(appContext)
        check(summary.pendingImports == 0) {
            "Wait for all wardrobe imports to finish before creating a backup."
        }
        check(summary.profileCount > 0) { "There are no wardrobes to back up." }

        val profilesState = File(appContext.filesDir, PROFILES_STATE_FILE)
        check(profilesState.isFile) { "The wardrobe profile list is missing." }

        val profiles = ProfileStorage.loadProfiles(appContext)
        val sourceFiles = backupSourceFiles(appContext, profiles)
        val manifest = WardrobeBackupManifest(
            formatVersion = CURRENT_BACKUP_VERSION,
            createdAt = System.currentTimeMillis(),
            appVersion = appVersion(appContext),
            profileCount = summary.profileCount,
            wardrobeItemCount = summary.wardrobeItemCount,
            outfitCount = summary.outfitCount
        )

        val totalFiles = sourceFiles.size + profiles.size + 1
        val totalBytes = sourceFiles.sumOf { it.second.length() }
        val output = appContext.contentResolver.openOutputStream(destination, "w")
            ?: error("Could not open the selected backup destination.")

        var filesCompleted = 0
        var bytesCompleted = 0L
        var lastProgressBytes = 0L

        ZipOutputStream(BufferedOutputStream(output)).use { zip ->
            val manifestBytes = manifestToJson(manifest).toString().toByteArray(Charsets.UTF_8)
            zip.putNextEntry(ZipEntry(MANIFEST_FILE))
            zip.write(manifestBytes)
            zip.closeEntry()
            filesCompleted++
            onProgress(BackupProgress(filesCompleted, totalFiles, bytesCompleted, totalBytes))

            profiles.forEach { profile ->
                zip.putNextEntry(ZipEntry("profiles/${profile.id}/"))
                zip.closeEntry()
                filesCompleted++
                onProgress(BackupProgress(filesCompleted, totalFiles, bytesCompleted, totalBytes))
            }

            sourceFiles.forEach { (archivePath, sourceFile) ->
                val entry = ZipEntry(archivePath).apply { time = sourceFile.lastModified() }
                zip.putNextEntry(entry)

                FileInputStream(sourceFile).use { input ->
                    val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                    while (true) {
                        val count = input.read(buffer)
                        if (count <= 0) break

                        zip.write(buffer, 0, count)
                        bytesCompleted += count

                        if (bytesCompleted - lastProgressBytes >= 1024L * 1024L) {
                            lastProgressBytes = bytesCompleted
                            onProgress(
                                BackupProgress(
                                    filesCompleted,
                                    totalFiles,
                                    bytesCompleted,
                                    totalBytes
                                )
                            )
                        }
                    }
                }

                zip.closeEntry()
                filesCompleted++
                onProgress(BackupProgress(filesCompleted, totalFiles, bytesCompleted, totalBytes))
            }
        }

        manifest
    }

    fun inspectBackup(context: Context, source: Uri): BackupInspection {
        val appContext = context.applicationContext
        val stagingRoot = File(
            appContext.cacheDir,
            "wardrobe_restore/${UUID.randomUUID()}"
        )
        val extractedRoot = File(stagingRoot, "contents")

        return try {
            extractedRoot.mkdirs()
            extractArchive(appContext, source, extractedRoot)

            val inspection = WardrobeBackupValidator.validate(extractedRoot)
            if (inspection.valid) {
                inspection.copy(stagingPath = stagingRoot.absolutePath)
            } else {
                stagingRoot.deleteRecursively()
                inspection
            }
        } catch (exception: Exception) {
            stagingRoot.deleteRecursively()
            BackupInspection(
                valid = false,
                errorMessage = exception.message ?: "The selected backup could not be read."
            )
        }
    }

    fun discardPreparedRestore(inspection: BackupInspection) {
        inspection.stagingPath?.let(::File)?.deleteRecursively()
    }

    fun restorePreparedBackup(
        context: Context,
        inspection: BackupInspection
    ): Result<Profile> = runCatching {
        val appContext = context.applicationContext
        check(inspection.valid) { "This backup has not passed validation." }

        val stagingRoot = inspection.stagingPath?.let(::File)
            ?: error("The prepared restore is no longer available.")
        val extractedRoot = File(stagingRoot, "contents")
        val validation = WardrobeBackupValidator.validate(extractedRoot)
        check(validation.valid) {
            validation.errorMessage ?: "The prepared backup is no longer valid."
        }

        val currentProfiles = ProfileStorage.loadProfiles(appContext)
        val pendingImports = currentProfiles.sumOf {
            WardrobeImportQueue.loadImports(appContext, it.id).size
        }
        check(pendingImports == 0) {
            "Wait for all wardrobe imports to finish before restoring a backup."
        }

        val preparedRoot = File(
            appContext.filesDir,
            ".restore_prepared_${UUID.randomUUID()}"
        )
        val rollbackRoot = File(
            appContext.filesDir,
            ".restore_rollback_${UUID.randomUUID()}"
        )

        preparedRoot.deleteRecursively()
        rollbackRoot.deleteRecursively()
        preparedRoot.mkdirs()

        copyRequiredRestoreFiles(extractedRoot, preparedRoot)
        rewritePortablePaths(appContext, preparedRoot)

        val preparedValidation = WardrobeBackupValidator.validate(preparedRoot)
        check(preparedValidation.valid) {
            preparedValidation.errorMessage ?: "The restored data failed its final validation."
        }

        rollbackRoot.mkdirs()

        val canonicalProfiles = File(appContext.filesDir, PROFILES_DIRECTORY)
        val canonicalState = File(appContext.filesDir, PROFILES_STATE_FILE)
        val rollbackProfiles = File(rollbackRoot, PROFILES_DIRECTORY)
        val rollbackState = File(rollbackRoot, PROFILES_STATE_FILE)
        var committed = false

        try {
            if (canonicalProfiles.exists()) movePath(canonicalProfiles, rollbackProfiles)
            if (canonicalState.exists()) movePath(canonicalState, rollbackState)

            movePath(File(preparedRoot, PROFILES_DIRECTORY), canonicalProfiles)
            movePath(File(preparedRoot, PROFILES_STATE_FILE), canonicalState)
            committed = true
        } finally {
            if (!committed) {
                canonicalProfiles.deleteRecursively()
                canonicalState.delete()
                if (rollbackProfiles.exists()) movePath(rollbackProfiles, canonicalProfiles)
                if (rollbackState.exists()) movePath(rollbackState, canonicalState)
            }
        }

        val restoredProfiles = ProfileStorage.loadProfiles(appContext)
        restoredProfiles.forEach { profile ->
            runCatching { syncWardrobeOutfitIds(appContext, profile.id) }
            WardrobeImportQueue.refresh(appContext, profile.id)
        }

        var activeProfile = ProfileStorage.loadActiveProfile(appContext)
        if (activeProfile == null) {
            val fallback = restoredProfiles.firstOrNull()
                ?: error("The restored backup contains no usable wardrobes.")
            check(ProfileStorage.setActiveProfile(appContext, fallback.id))
            activeProfile = fallback
        }

        rollbackRoot.deleteRecursively()
        preparedRoot.deleteRecursively()
        stagingRoot.deleteRecursively()

        checkNotNull(activeProfile)
    }

    fun recoverInterruptedRestore(context: Context) {
        val appContext = context.applicationContext
        val rollbackRoots = appContext.filesDir.listFiles()
            ?.filter { it.isDirectory && it.name.startsWith(".restore_rollback_") }
            ?.sortedByDescending { it.lastModified() }
            .orEmpty()

        rollbackRoots.forEach { rollbackRoot ->
            val canonicalProfiles = File(appContext.filesDir, PROFILES_DIRECTORY)
            val canonicalState = File(appContext.filesDir, PROFILES_STATE_FILE)
            val rollbackProfiles = File(rollbackRoot, PROFILES_DIRECTORY)
            val rollbackState = File(rollbackRoot, PROFILES_STATE_FILE)

            if (canonicalProfilesLookUsable(appContext.filesDir)) {
                rollbackRoot.deleteRecursively()
            } else {
                canonicalProfiles.deleteRecursively()
                canonicalState.delete()

                if (rollbackProfiles.exists()) {
                    runCatching { movePath(rollbackProfiles, canonicalProfiles) }
                }
                if (rollbackState.exists()) {
                    runCatching { movePath(rollbackState, canonicalState) }
                }
                rollbackRoot.deleteRecursively()
            }
        }

        appContext.filesDir.listFiles()
            ?.filter { it.isDirectory && it.name.startsWith(".restore_prepared_") }
            ?.forEach { it.deleteRecursively() }
    }

    private fun canonicalProfilesLookUsable(filesDir: File): Boolean {
        val profilesState = File(filesDir, PROFILES_STATE_FILE)
        val profilesDirectory = File(filesDir, PROFILES_DIRECTORY)
        if (!profilesState.isFile || !profilesDirectory.isDirectory) return false

        return runCatching {
            val json = JSONObject(profilesState.readText())
            val profiles = json.optJSONArray("profiles") ?: return@runCatching false
            if (profiles.length() == 0) return@runCatching false

            val ids = mutableSetOf<String>()
            for (index in 0 until profiles.length()) {
                val id = profiles.getJSONObject(index).getString("id")
                if (!File(profilesDirectory, id).isDirectory) return@runCatching false
                ids += id
            }

            val activeId = json.optString("activeProfileId").takeIf { it.isNotBlank() }
            activeId != null && activeId in ids
        }.getOrDefault(false)
    }

    private fun backupSourceFiles(
        context: Context,
        profiles: List<Profile>
    ): List<Pair<String, File>> {
        val files = mutableListOf<Pair<String, File>>()
        val profilesState = File(context.filesDir, PROFILES_STATE_FILE)
        if (profilesState.isFile) files += PROFILES_STATE_FILE to profilesState

        profiles.forEach { profile ->
            val profileDirectory = ProfileStorage.profileDirectory(context, profile.id)

            profileDataDirectories.forEach { directoryName ->
                val directory = File(profileDirectory, directoryName)
                if (directory.isDirectory) {
                    directory.walkTopDown()
                        .filter { it.isFile }
                        .forEach { file ->
                            val relativePath = file.relativeTo(profileDirectory)
                                .path
                                .replace(File.separatorChar, '/')
                            files += "profiles/${profile.id}/$relativePath" to file
                        }
                }
            }
        }

        return files.sortedBy { it.first }
    }

    private fun extractArchive(
        context: Context,
        source: Uri,
        destinationRoot: File
    ) {
        val root = destinationRoot.canonicalFile
        val input = context.contentResolver.openInputStream(source)
            ?: error("Could not open the selected backup.")

        var entryCount = 0
        var extractedBytes = 0L

        ZipInputStream(BufferedInputStream(input)).use { zip ->
            while (true) {
                val entry = zip.nextEntry ?: break
                entryCount++
                check(entryCount <= MAX_ARCHIVE_ENTRIES) {
                    "This backup contains too many files."
                }

                val entryName = entry.name.replace('\\', '/')
                check(entryName.isNotBlank() && !entryName.startsWith("/")) {
                    "This backup contains an unsafe file path."
                }

                val target = File(root, entryName).canonicalFile
                val allowedPrefix = root.path + File.separator
                check(target != root && target.path.startsWith(allowedPrefix)) {
                    "This backup contains an unsafe file path."
                }

                if (entry.isDirectory) {
                    target.mkdirs()
                } else {
                    check(!target.exists()) {
                        "This backup contains duplicate file entries."
                    }
                    target.parentFile?.mkdirs()

                    FileOutputStream(target).use { output ->
                        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                        while (true) {
                            val count = zip.read(buffer)
                            if (count <= 0) break

                            extractedBytes += count
                            check(extractedBytes <= MAX_EXTRACTED_BYTES) {
                                "This backup is too large to restore safely."
                            }
                            output.write(buffer, 0, count)
                        }
                    }

                    if (entry.time > 0L) target.setLastModified(entry.time)
                }

                zip.closeEntry()
            }
        }
    }

    private fun copyRequiredRestoreFiles(sourceRoot: File, destinationRoot: File) {
        val manifest = File(sourceRoot, MANIFEST_FILE)
        val profilesState = File(sourceRoot, PROFILES_STATE_FILE)
        val profilesDirectory = File(sourceRoot, PROFILES_DIRECTORY)
        check(manifest.isFile && profilesState.isFile && profilesDirectory.isDirectory) {
            "The prepared backup is incomplete."
        }

        manifest.copyTo(File(destinationRoot, MANIFEST_FILE), overwrite = true)
        profilesState.copyTo(File(destinationRoot, PROFILES_STATE_FILE), overwrite = true)
        check(
            profilesDirectory.copyRecursively(
                target = File(destinationRoot, PROFILES_DIRECTORY),
                overwrite = true
            )
        ) {
            "Could not prepare the restored wardrobe data."
        }
    }

    private fun rewritePortablePaths(context: Context, preparedRoot: File) {
        val profilesState = JSONObject(File(preparedRoot, PROFILES_STATE_FILE).readText())
        val profiles = profilesState.optJSONArray("profiles")
            ?: error("The prepared backup contains no wardrobes.")

        for (index in 0 until profiles.length()) {
            val profileId = profiles.getJSONObject(index).getString("id")
            val preparedProfile = File(preparedRoot, "profiles/$profileId")
            val finalProfile = ProfileStorage.profileDirectory(context, profileId)

            rewriteWardrobeItemPaths(preparedProfile, finalProfile)
            rewriteOutfitPreviewPaths(preparedProfile, finalProfile)
        }
    }

    private fun rewriteWardrobeItemPaths(
        preparedProfileDirectory: File,
        finalProfileDirectory: File
    ) {
        val itemDirectory = File(preparedProfileDirectory, "wardrobe_items")
        if (!itemDirectory.isDirectory) return

        val imagesDirectory = File(preparedProfileDirectory, "wardrobe_images")
        val cutoutsDirectory = File(preparedProfileDirectory, "wardrobe_cutouts")

        itemDirectory.listFiles()
            ?.filter { it.isFile && it.extension == "json" }
            ?.forEach { itemFile ->
                val json = JSONObject(itemFile.readText())
                val itemId = json.getString("id")
                val imageFile = findFileForId(imagesDirectory, itemId)
                    ?: error("Restored item $itemId is missing its original image.")

                json.put(
                    "imagePath",
                    File(finalProfileDirectory, "wardrobe_images/${imageFile.name}").absolutePath
                )

                val cutoutFile = findFileForId(cutoutsDirectory, itemId)
                json.put(
                    "cutoutPath",
                    cutoutFile?.let {
                        File(finalProfileDirectory, "wardrobe_cutouts/${it.name}").absolutePath
                    } ?: JSONObject.NULL
                )

                itemFile.writeText(json.toString())
            }
    }

    private fun rewriteOutfitPreviewPaths(
        preparedProfileDirectory: File,
        finalProfileDirectory: File
    ) {
        val outfitsDirectory = File(preparedProfileDirectory, "outfits")
        if (!outfitsDirectory.isDirectory) return

        val previewsDirectory = File(preparedProfileDirectory, "outfit_previews")

        outfitsDirectory.listFiles()
            ?.filter { it.isFile && it.extension == "json" }
            ?.forEach { outfitFile ->
                val json = JSONObject(outfitFile.readText())
                val previousPath = if (json.isNull("previewPath")) {
                    null
                } else {
                    json.optString("previewPath").takeIf { it.isNotBlank() }
                }

                val previewFile = previousPath
                    ?.let { File(previewsDirectory, File(it).name) }
                    ?.takeIf { it.isFile }

                json.put(
                    "previewPath",
                    previewFile?.let {
                        File(finalProfileDirectory, "outfit_previews/${it.name}").absolutePath
                    } ?: JSONObject.NULL
                )

                outfitFile.writeText(json.toString())
            }
    }

    private fun findFileForId(directory: File, id: String): File? {
        if (!directory.isDirectory) return null
        return directory.listFiles()
            ?.firstOrNull { it.isFile && it.nameWithoutExtension == id }
    }

    private fun movePath(source: File, target: File) {
        target.parentFile?.mkdirs()
        if (source.renameTo(target)) return

        if (source.isDirectory) {
            check(source.copyRecursively(target, overwrite = true)) {
                "Could not move restored wardrobe data."
            }
            source.deleteRecursively()
        } else {
            source.copyTo(target, overwrite = true)
            check(source.delete()) { "Could not finish moving restored wardrobe data." }
        }
    }

    private fun manifestToJson(manifest: WardrobeBackupManifest): JSONObject {
        return JSONObject()
            .put("formatVersion", manifest.formatVersion)
            .put("createdAt", manifest.createdAt)
            .put("appVersion", manifest.appVersion)
            .put("profileCount", manifest.profileCount)
            .put("wardrobeItemCount", manifest.wardrobeItemCount)
            .put("outfitCount", manifest.outfitCount)
    }

    @Suppress("DEPRECATION")
    private fun appVersion(context: Context): String {
        return runCatching {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "unknown"
        }.getOrDefault("unknown")
    }
}
