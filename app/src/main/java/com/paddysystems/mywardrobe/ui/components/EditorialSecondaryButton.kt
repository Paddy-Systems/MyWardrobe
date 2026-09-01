package com.paddysystems.mywardrobe.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun EditorialSecondaryButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true, icon: ImageVector? = null) {
    OutlinedButton(onClick, modifier, enabled, contentPadding = PaddingValues(horizontal = 22.dp, vertical = 15.dp)) {
        icon?.let { Icon(it, null, Modifier.padding(end = 8.dp)) }
        Text(text.uppercase(), style = MaterialTheme.typography.labelLarge)
    }
}
