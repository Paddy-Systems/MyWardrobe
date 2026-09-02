package com.paddysystems.wearfolio.ui.screens.profiles

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.SettingsBackupRestore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.paddysystems.wearfolio.data.model.Profile
import com.paddysystems.wearfolio.data.storage.ProfileStorage
import com.paddysystems.wearfolio.ui.LocalActiveProfile
import com.paddysystems.wearfolio.ui.components.EditorialPageHeader
import com.paddysystems.wearfolio.ui.components.EditorialSecondaryButton
import com.paddysystems.wearfolio.ui.screens.onboarding.WardrobeNameForm

@Composable
fun ManageProfilesScreen(
    onBack: () -> Unit,
    onProfileSwitched: (Profile) -> Unit,
    onDataManagement: () -> Unit = {},
    onAccount: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activeProfile = LocalActiveProfile.current

    var profiles by remember {
        mutableStateOf(
            ProfileStorage.loadProfiles(
                context
            )
        )
    }

    var showAddWardrobe by remember {
        mutableStateOf(false)
    }

    var newWardrobeName by remember {
        mutableStateOf("")
    }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(
                    MaterialTheme
                        .colorScheme
                        .background
                )
                .padding(
                    horizontal = 20.dp,
                    vertical = 22.dp
                )
    ) {
        EditorialPageHeader(
            eyebrow =
                "Wardrobes",
            title =
                "Your wardrobes",
            subtitle =
                "Switch collections, add another wardrobe, or keep every wardrobe backed up.",
            navigationIcon =
                Icons.AutoMirrored
                    .Outlined
                    .ArrowBack,
            onNavigate =
                onBack
        )

        Spacer(
            Modifier.height(
                20.dp
            )
        )

        profiles
            .forEach { profile ->
                val isActive =
                    profile.id ==
                        activeProfile.id

                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .clip(
                                RoundedCornerShape(
                                    14.dp
                                )
                            )
                            .clickable(
                                enabled =
                                    !isActive
                            ) {
                                if (
                                    ProfileStorage
                                        .setActiveProfile(
                                            context,
                                            profile.id
                                        )
                                ) {
                                    onProfileSwitched(
                                        profile
                                    )
                                }
                            }
                            .padding(
                                vertical = 14.dp,
                                horizontal = 12.dp
                            ),
                    horizontalArrangement =
                        Arrangement
                            .SpaceBetween,
                    verticalAlignment =
                        Alignment
                            .CenterVertically
                ) {
                    Text(
                        text =
                            "${profile.name}'s Wardrobe",
                        style =
                            MaterialTheme
                                .typography
                                .bodyLarge,
                        color =
                            MaterialTheme
                                .colorScheme
                                .onBackground
                    )

                    if (
                        isActive
                    ) {
                        Icon(
                            imageVector =
                                Icons.Outlined
                                    .Check,
                            contentDescription =
                                "Currently active",
                            tint =
                                MaterialTheme
                                    .colorScheme
                                    .primary
                        )
                    }
                }
            }

        Spacer(
            Modifier.height(
                12.dp
            )
        )

        if (
            showAddWardrobe
        ) {
            WardrobeNameForm(
                name =
                    newWardrobeName,
                onNameChange = {
                    newWardrobeName =
                        it
                },
                submitLabel =
                    "Add wardrobe",
                onSubmit = {
                    val profile =
                        ProfileStorage
                            .createProfile(
                                context,
                                newWardrobeName
                                    .trim()
                            )

                    profiles =
                        ProfileStorage
                            .loadProfiles(
                                context
                            )

                    newWardrobeName = ""
                    showAddWardrobe = false

                    onProfileSwitched(
                        profile
                    )
                }
            )
        } else {
            EditorialSecondaryButton(
                text =
                    "Add another wardrobe",
                onClick = {
                    showAddWardrobe =
                        true
                },
                modifier =
                    Modifier.fillMaxWidth()
            )
        }

        Spacer(
            Modifier.height(
                12.dp
            )
        )

        EditorialSecondaryButton(
            text =
                "Wearfolio account",
            icon =
                Icons.Outlined
                    .AccountCircle,
            onClick =
                onAccount,
            modifier =
                Modifier.fillMaxWidth()
        )

        Spacer(
            Modifier.height(
                12.dp
            )
        )

        EditorialSecondaryButton(
            text =
                "Backup & restore",
            icon =
                Icons.Outlined
                    .SettingsBackupRestore,
            onClick =
                onDataManagement,
            modifier =
                Modifier.fillMaxWidth()
        )
    }
}
