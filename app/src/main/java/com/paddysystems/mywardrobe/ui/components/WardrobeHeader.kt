package com.paddysystems.mywardrobe.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun WardrobeHeader(
    itemCount: Int,
    totalItemCount: Int,
    selectedCount: Int,
    modifier: Modifier = Modifier
) {
    val text =
        if (selectedCount > 0) {
            "$selectedCount selected"
        } else if (
            itemCount == totalItemCount
        ) {
            "My Wardrobe · " +
                    "$totalItemCount " +
                    itemWord(totalItemCount)
        } else {
            "My Wardrobe · " +
                    "$itemCount of " +
                    "$totalItemCount " +
                    itemWord(totalItemCount)
        }

    Text(
        text = text,
        style =
            MaterialTheme
                .typography
                .headlineLarge,
        modifier = modifier
    )
}

private fun itemWord(
    count: Int
): String {
    return if (count == 1) {
        "item"
    } else {
        "items"
    }
}