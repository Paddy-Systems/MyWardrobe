package com.paddysystems.wearfolio.ui.screens.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.paddysystems.wearfolio.data.model.Profile
import com.paddysystems.wearfolio.data.storage.ProfileStorage
import com.paddysystems.wearfolio.ui.components.EditorialPageHeader
import com.paddysystems.wearfolio.ui.components.WearfolioLockup

@Composable
fun OnboardingScreen(
    onProfileCreated: (Profile) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(horizontal = 20.dp, vertical = 22.dp)
    ) {
        WearfolioLockup()

        Spacer(Modifier.height(32.dp))

        EditorialPageHeader(
            eyebrow = "Your wardrobe, considered",
            title = "Whose wardrobe is this?",
            subtitle = "Give it a name so you can tell your wardrobes apart later."
        )

        Spacer(Modifier.height(24.dp))

        WardrobeNameForm(
            name = name,
            onNameChange = { name = it },
            submitLabel = "Create Wearfolio",
            onSubmit = {
                val profile = ProfileStorage.createProfile(context, name.trim())
                onProfileCreated(profile)
            }
        )
    }
}
