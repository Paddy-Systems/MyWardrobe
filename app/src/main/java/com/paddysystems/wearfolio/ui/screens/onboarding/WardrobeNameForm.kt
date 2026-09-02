package com.paddysystems.wearfolio.ui.screens.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paddysystems.wearfolio.ui.components.EditorialPrimaryButton

@Composable
fun WardrobeNameForm(
    name: String,
    onNameChange: (String) -> Unit,
    submitLabel: String,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        val previewName = name.trim()

        Text(
            text = if (previewName.isEmpty()) {
                "Their wardrobe"
            } else {
                "$previewName's Wardrobe"
            },
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 16.dp, bottom = 24.dp)
        )

        EditorialPrimaryButton(
            text = submitLabel,
            onClick = onSubmit,
            enabled = previewName.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
