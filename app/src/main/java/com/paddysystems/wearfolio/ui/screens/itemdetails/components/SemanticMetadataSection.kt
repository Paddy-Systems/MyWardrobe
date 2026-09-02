package com.paddysystems.wearfolio.ui.screens.itemdetails.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paddysystems.wearfolio.data.model.SemanticTag
import com.paddysystems.wearfolio.data.model.WardrobeMetadata
import com.paddysystems.wearfolio.data.model.wardrobeLabel
import com.paddysystems.wearfolio.search.WardrobeSemanticSignals

@Composable
fun SemanticMetadataSection(
    metadata: WardrobeMetadata,
    modifier: Modifier = Modifier
) {
    val sections =
        listOf(
            "Pattern" to
                    WardrobeSemanticSignals
                        .strongTags(
                            metadata.patterns
                        ),

            "Material" to
                    WardrobeSemanticSignals
                        .strongTags(
                            metadata.materials
                        ),

            "Style" to
                    WardrobeSemanticSignals
                        .strongTags(
                            metadata.styles
                        ),

            "Occasion" to
                    WardrobeSemanticSignals
                        .strongTags(
                            metadata.occasions
                        ),

            "Season" to
                    WardrobeSemanticSignals
                        .strongTags(
                            metadata.seasons
                        ),

            "Formality" to
                    WardrobeSemanticSignals
                        .strongTags(
                            metadata.formalities
                        )
        )
            .filter {
                it.second.isNotEmpty()
            }

    Column(
        modifier =
            modifier.fillMaxWidth()
    ) {
        Text(
            text =
                "Detected metadata",

            style =
                MaterialTheme
                    .typography
                    .titleMedium
        )

        Text(
            text =
                "Detected from the photo " +
                        "for search and filtering",

            style =
                MaterialTheme
                    .typography
                    .bodySmall,

            color =
                MaterialTheme
                    .colorScheme
                    .onSurfaceVariant
        )

        if (sections.isEmpty()) {
            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )

            Text(
                text =
                    "No semantic metadata available",

                style =
                    MaterialTheme
                        .typography
                        .bodyMedium
            )

            return
        }

        sections.forEach {
                (title, tags) ->

            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )

            SemanticTagRow(
                title = title,
                tags = tags
            )
        }
    }
}

@Composable
private fun SemanticTagRow(
    title: String,
    tags: List<SemanticTag>
) {
    Column {
        Text(
            text = title,
            style =
                MaterialTheme
                    .typography
                    .labelLarge
        )

        LazyRow(
            horizontalArrangement =
                Arrangement.spacedBy(
                    8.dp
                ),

            contentPadding =
                PaddingValues(
                    vertical = 6.dp
                )
        ) {
            items(
                items = tags,
                key = {
                    it.id
                }
            ) { tag ->

                Surface(
                    shape =
                        RoundedCornerShape(
                            50
                        ),

                    color =
                        MaterialTheme
                            .colorScheme
                            .surfaceVariant
                ) {
                    Text(
                        text =
                            wardrobeLabel(
                                tag.id
                            ),

                        modifier =
                            Modifier.padding(
                                horizontal =
                                    12.dp,

                                vertical =
                                    7.dp
                            ),

                        style =
                            MaterialTheme
                                .typography
                                .bodyMedium
                    )
                }
            }
        }
    }
}