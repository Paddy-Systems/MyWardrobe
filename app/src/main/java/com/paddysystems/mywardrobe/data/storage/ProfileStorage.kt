package com.paddysystems.mywardrobe.data.storage

import android.content.Context
import com.paddysystems.mywardrobe.data.model.Profile
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.UUID

object ProfileStorage {

    fun profileDirectory(
        context: Context,
        profileId: String
    ): File {
        return File(context.filesDir, "profiles/$profileId")
    }

    fun loadProfiles(context: Context): List<Profile> {
        return readState(context).profiles
    }

    fun loadActiveProfile(context: Context): Profile? {
        val state = readState(context)
        return state.profiles.firstOrNull { it.id == state.activeProfileId }
    }

    fun createProfile(
        context: Context,
        name: String
    ): Profile {
        val state = readState(context)

        val profile = Profile(
            id = UUID.randomUUID().toString(),
            name = name,
            createdAt = System.currentTimeMillis()
        )

        profileDirectory(context, profile.id).mkdirs()

        val updatedState = state.copy(
            activeProfileId = profile.id,
            profiles = state.profiles + profile
        )

        writeState(context, updatedState)
        return profile
    }

    fun setActiveProfile(
        context: Context,
        profileId: String
    ): Boolean {
        val state = readState(context)

        if (state.profiles.none { it.id == profileId }) {
            return false
        }

        writeState(context, state.copy(activeProfileId = profileId))
        return true
    }

    private data class ProfilesState(
        val activeProfileId: String?,
        val profiles: List<Profile>
    )

    private fun stateFile(context: Context): File {
        return File(context.filesDir, "profiles.json")
    }

    private fun readState(context: Context): ProfilesState {
        val file = stateFile(context)

        if (!file.exists()) {
            return ProfilesState(activeProfileId = null, profiles = emptyList())
        }

        return try {
            val json = JSONObject(file.readText())
            val profilesArray = json.optJSONArray("profiles")

            val profiles = if (profilesArray == null) {
                emptyList()
            } else {
                List(profilesArray.length()) { index ->
                    val profileJson = profilesArray.getJSONObject(index)

                    Profile(
                        id = profileJson.getString("id"),
                        name = profileJson.getString("name"),
                        createdAt = profileJson.optLong("createdAt", 0L)
                    )
                }
            }

            ProfilesState(
                activeProfileId = json.optString("activeProfileId").takeIf { it.isNotBlank() },
                profiles = profiles
            )
        } catch (exception: Exception) {
            ProfilesState(activeProfileId = null, profiles = emptyList())
        }
    }

    private fun writeState(
        context: Context,
        state: ProfilesState
    ) {
        val json = JSONObject()
            .put("activeProfileId", state.activeProfileId ?: JSONObject.NULL)
            .put(
                "profiles",
                JSONArray().apply {
                    state.profiles.forEach { profile ->
                        put(
                            JSONObject()
                                .put("id", profile.id)
                                .put("name", profile.name)
                                .put("createdAt", profile.createdAt)
                        )
                    }
                }
            )

        stateFile(context).writeText(json.toString())
    }
}
