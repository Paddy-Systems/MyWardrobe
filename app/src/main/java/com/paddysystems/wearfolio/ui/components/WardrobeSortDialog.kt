package com.paddysystems.wearfolio.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paddysystems.wearfolio.search.WardrobeSortOrder

@Composable
fun WardrobeSortDialog(
    selectedOrder: WardrobeSortOrder,
    onSelect: (WardrobeSortOrder) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text("Sort wardrobe")
        },

        text = {
            Column {
                WardrobeSortOrder.entries
                    .forEach { order ->

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSelect(order)
                                }
                                .padding(
                                    vertical = 6.dp
                                ),
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected =
                                    order ==
                                            selectedOrder,

                                onClick = {
                                    onSelect(order)
                                }
                            )

                            Text(
                                order.label
                            )
                        }
                    }
            }
        },

        confirmButton = {},

        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
}