package com.paddysystems.mywardrobe.ui.screens.createoutfit.components

import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap

@Composable
fun OutfitSilhouette(
    modifier: Modifier = Modifier
) {
    val colour =
        MaterialTheme
            .colorScheme
            .onSurfaceVariant
            .copy(
                alpha = 0.12f
            )

    Canvas(
        modifier = modifier
    ) {
        val width =
            size.width

        val height =
            size.height

        val centerX =
            width / 2f

        // Head
        drawCircle(
            color = colour,
            radius =
                width * 0.095f,
            center =
                Offset(
                    centerX,
                    height * 0.09f
                )
        )

        // Torso
        drawRoundRect(
            color = colour,

            topLeft =
                Offset(
                    width * 0.34f,
                    height * 0.18f
                ),

            size =
                Size(
                    width * 0.32f,
                    height * 0.34f
                ),

            cornerRadius =
                CornerRadius(
                    width * 0.08f
                )
        )

        // Arms
        drawLine(
            color = colour,

            start =
                Offset(
                    width * 0.36f,
                    height * 0.23f
                ),

            end =
                Offset(
                    width * 0.23f,
                    height * 0.56f
                ),

            strokeWidth =
                width * 0.09f,

            cap =
                StrokeCap.Round
        )

        drawLine(
            color = colour,

            start =
                Offset(
                    width * 0.64f,
                    height * 0.23f
                ),

            end =
                Offset(
                    width * 0.77f,
                    height * 0.56f
                ),

            strokeWidth =
                width * 0.09f,

            cap =
                StrokeCap.Round
        )

        // Legs
        drawLine(
            color = colour,

            start =
                Offset(
                    width * 0.44f,
                    height * 0.49f
                ),

            end =
                Offset(
                    width * 0.39f,
                    height * 0.91f
                ),

            strokeWidth =
                width * 0.105f,

            cap =
                StrokeCap.Round
        )

        drawLine(
            color = colour,

            start =
                Offset(
                    width * 0.56f,
                    height * 0.49f
                ),

            end =
                Offset(
                    width * 0.61f,
                    height * 0.91f
                ),

            strokeWidth =
                width * 0.105f,

            cap =
                StrokeCap.Round
        )
    }
}