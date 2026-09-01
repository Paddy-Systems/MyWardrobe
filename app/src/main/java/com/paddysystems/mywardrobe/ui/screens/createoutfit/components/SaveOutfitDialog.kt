package com.paddysystems.mywardrobe.ui.screens.createoutfit.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun SaveOutfitDialog(
    suggestedName: String,
    onSave: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember(
        suggestedName
    ) {
        mutableStateOf(
            suggestedName
        )
    }

    AlertDialog(
        onDismissRequest =
            onDismiss,

        title = {
            Text("Save Outfit")
        },

        text = {
            OutlinedTextField(
                value = name,

                onValueChange = {
                    name = it
                },

                label = {
                    Text("Outfit name")
                },

                singleLine = true
            )
        },

        confirmButton = {
            TextButton(
                enabled =
                    name.isNotBlank(),

                onClick = {
                    onSave(
                        name.trim()
                    )
                }
            ) {
                Text("Save")
            }
        },

        dismissButton = {
            TextButton(
                onClick =
                    onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
}