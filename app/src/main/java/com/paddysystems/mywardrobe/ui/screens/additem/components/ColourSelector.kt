package com.paddysystems.mywardrobe.ui.screens.additem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private val availableColours = listOf(
    "black",
    "white",
    "grey",
    "blue",
    "navy",
    "red",
    "green",
    "yellow",
    "orange",
    "pink",
    "purple",
    "brown",
    "beige",
    "cream",
    "teal"
)

@Composable
fun ColourSelector(
    selectedColours: List<String>,
    onColoursChanged: (List<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            "Colours (${selectedColours.size}/3)"
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(
                vertical = 8.dp
            )
        ) {
            items(availableColours) { colour ->
                val selected =
                    colour in selectedColours

                FilterChip(
                    selected = selected,
                    onClick = {
                        val updatedColours =
                            if (selected) {
                                selectedColours - colour
                            } else {
                                if (selectedColours.size >= 3) {
                                    selectedColours
                                } else {
                                    selectedColours + colour
                                }
                            }

                        onColoursChanged(
                            updatedColours
                        )
                    },
                    label = {
                        Text(
                            colour.replaceFirstChar {
                                it.uppercase()
                            }
                        )
                    }
                )
            }
        }
    }
}