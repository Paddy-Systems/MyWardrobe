package com.paddysystems.wearfolio.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

@Composable
fun DeleteConfirmationDialog(
    selectedCount: Int,
    affectedOutfitCount: Int = 0,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (selectedCount == 0) {
        return
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (selectedCount == 1) {
                    "Remove item?"
                } else {
                    "Remove $selectedCount items?"
                },
                style = MaterialTheme.typography.headlineMedium
            )
        },
        text = {
            Text(
                buildString {
                    append(
                        if (selectedCount == 1) {
                            "This will permanently remove this item from your wardrobe."
                        } else {
                            "This will permanently remove $selectedCount items from your wardrobe."
                        }
                    )

                    if (affectedOutfitCount > 0) {
                        append("\n\n")
                        append(
                            if (affectedOutfitCount == 1) {
                                "1 saved fit uses the selected item. It will be updated automatically."
                            } else {
                                "$affectedOutfitCount saved fits use the selected items. They will be updated automatically."
                            }
                        )
                        append(" The saved fits themselves will not be deleted.")
                    }
                }
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Remove")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(28.dp),
        containerColor = MaterialTheme.colorScheme.background
    )
}
