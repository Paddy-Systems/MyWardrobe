package com.paddysystems.mywardrobe.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun DeleteConfirmationDialog(
    selectedCount: Int,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (selectedCount == 0) {
        return
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Remove $selectedCount items?")
        },
        text = {
            Text(
                "This will permanently remove $selectedCount items from your wardrobe."
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                Text("Remove")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
}