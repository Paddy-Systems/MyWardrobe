package com.paddysystems.mywardrobe.ui.screens.additem.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.paddysystems.mywardrobe.data.model.CropType

@Composable
fun CropEditor(
    imageUri: Uri,
    cropType: CropType,
    modifier: Modifier = Modifier
) {
    var scale by remember(imageUri, cropType) {
        mutableFloatStateOf(1f)
    }

    var offset by remember(imageUri, cropType) {
        mutableStateOf(Offset.Zero)
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Black)
    ) {
        val cropHeight = maxHeight * 0.84f

        val cropWidth = when (cropType) {
            CropType.FULL_BODY -> cropHeight * 0.5f

            CropType.HALF_PIECE -> minOf(
                maxWidth * 0.84f,
                cropHeight
            )
        }

        val density = LocalDensity.current

        val editorWidthPx = with(density) {
            maxWidth.toPx()
        }

        val editorHeightPx = with(density) {
            maxHeight.toPx()
        }

        val cropWidthPx = with(density) {
            cropWidth.toPx()
        }

        val cropHeightPx = with(density) {
            cropHeight.toPx()
        }

        AsyncImage(
            model = imageUri,
            contentDescription = "Crop image",
            modifier = Modifier
                .matchParentSize()
                .pointerInput(
                    editorWidthPx,
                    editorHeightPx,
                    cropWidthPx,
                    cropHeightPx
                ) {
                    detectTransformGestures { _, pan, zoom, _ ->

                        val newScale = (scale * zoom)
                            .coerceIn(1f, 5f)

                        val maxOffsetX =
                            ((editorWidthPx * newScale) - cropWidthPx) / 2f

                        val maxOffsetY =
                            ((editorHeightPx * newScale) - cropHeightPx) / 2f

                        val newOffset = offset + pan

                        offset = Offset(
                            x = newOffset.x.coerceIn(
                                -maxOffsetX,
                                maxOffsetX
                            ),
                            y = newOffset.y.coerceIn(
                                -maxOffsetY,
                                maxOffsetY
                            )
                        )

                        scale = newScale
                    }
                }
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y
                ),
            contentScale = ContentScale.Crop
        )

        val shade = Color.Black.copy(
            alpha = 0.55f
        )

        val verticalShadeHeight =
            (maxHeight - cropHeight) / 2f

        val horizontalShadeWidth =
            (maxWidth - cropWidth) / 2f

        // Top shaded area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(verticalShadeHeight)
                .align(Alignment.TopCenter)
                .background(shade)
        )

        // Bottom shaded area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(verticalShadeHeight)
                .align(Alignment.BottomCenter)
                .background(shade)
        )

        // Left shaded area
        Box(
            modifier = Modifier
                .width(horizontalShadeWidth)
                .height(cropHeight)
                .align(Alignment.CenterStart)
                .background(shade)
        )

        // Right shaded area
        Box(
            modifier = Modifier
                .width(horizontalShadeWidth)
                .height(cropHeight)
                .align(Alignment.CenterEnd)
                .background(shade)
        )

        // Actual crop boundary
        Box(
            modifier = Modifier
                .size(
                    width = cropWidth,
                    height = cropHeight
                )
                .align(Alignment.Center)
                .border(
                    width = 2.dp,
                    color = Color.White,
                    shape = RoundedCornerShape(8.dp)
                )
        )
    }
}