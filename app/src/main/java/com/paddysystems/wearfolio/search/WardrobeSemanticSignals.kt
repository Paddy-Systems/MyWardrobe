package com.paddysystems.wearfolio.search

import com.paddysystems.wearfolio.data.model.SemanticTag
import com.paddysystems.wearfolio.data.model.WardrobeItem

object WardrobeSemanticSignals {

    private const val MIN_SIMILARITY =
        0.015f

    private const val MAX_TAGS_PER_CATEGORY =
        3

    fun strongTags(
        tags: List<SemanticTag>
    ): List<SemanticTag> {
        return tags
            .take(
                MAX_TAGS_PER_CATEGORY
            )
            .filter {
                it.similarity >=
                        MIN_SIMILARITY
            }
    }

    fun allStrongTags(
        item: WardrobeItem
    ): List<SemanticTag> {
        return buildList {

            addAll(
                strongTags(
                    item.metadata.patterns
                )
            )

            addAll(
                strongTags(
                    item.metadata.materials
                )
            )

            addAll(
                strongTags(
                    item.metadata.styles
                )
            )

            addAll(
                strongTags(
                    item.metadata.occasions
                )
            )

            addAll(
                strongTags(
                    item.metadata.seasons
                )
            )

            addAll(
                strongTags(
                    item.metadata.formalities
                )
            )
        }
    }

    fun matchesAny(
        tags: List<SemanticTag>,
        selectedIds: Set<String>
    ): Boolean {

        if (selectedIds.isEmpty()) {
            return true
        }

        return strongTags(tags)
            .any {
                it.id in selectedIds
            }
    }
}