package com.paddysystems.mywardrobe.search

import com.paddysystems.mywardrobe.data.model.SemanticTag
import com.paddysystems.mywardrobe.data.model.WardrobeItem

data class WardrobeSearchResult(
    val item: WardrobeItem,
    val score: Float
)

object WardrobeSearchEngine {

    private const val TYPE_SCORE =
        100f

    private const val COLOUR_SCORE =
        80f

    private const val SEMANTIC_BASE_SCORE =
        20f

    /*
     * Low positive FashionSigLIP similarities can
     * just be noise.
     *
     * Your T-shirt, for example:
     *
     * summer  = .043
     * spring  = .027
     * autumn  = .013
     * winter  = .005
     *
     * So .015 is a sensible initial floor.
     * We can tune this later from real wardrobe data.
     */
    private const val MIN_SEMANTIC_SIMILARITY =
        0.015f

    fun search(
        items: List<WardrobeItem>,
        query: String
    ): List<WardrobeSearchResult> {

        val tokens =
            canonicalizeQuery(query)

        if (tokens.isEmpty()) {
            return items.map { item ->
                WardrobeSearchResult(
                    item = item,
                    score = 0f
                )
            }
        }

        return items
            .mapNotNull { item ->

                val tokenScores =
                    tokens.map { token ->
                        scoreToken(
                            item = item,
                            token = token
                        )
                            ?: return@mapNotNull null
                    }

                WardrobeSearchResult(
                    item = item,
                    score = tokenScores.sum()
                )
            }
            .sortedWith(
                compareByDescending<
                        WardrobeSearchResult
                        > {
                    it.score
                }.thenByDescending {
                    it.item.createdAt
                }
            )
    }

    private fun scoreToken(
        item: WardrobeItem,
        token: String
    ): Float? {

        var bestScore: Float? = null

        if (
            item.clothingTypeId == token
        ) {
            bestScore = TYPE_SCORE
        }

        if (
            item.colours.any {
                it == token
            }
        ) {
            bestScore =
                maxOf(
                    bestScore ?: 0f,
                    COLOUR_SCORE
                )
        }

        semanticTags(item)
            .filter {
                it.id == token
            }
            .filter {
                it.similarity >=
                        MIN_SEMANTIC_SIMILARITY
            }
            .forEach { tag ->

                val semanticScore =
                    SEMANTIC_BASE_SCORE +
                            (
                                    tag.similarity *
                                            100f
                                    )

                bestScore =
                    maxOf(
                        bestScore ?: 0f,
                        semanticScore
                    )
            }

        return bestScore
    }

    /*
     * We intentionally only search the strongest
     * three candidates from each semantic family.
     *
     * The metadata file keeps more candidates because
     * they're useful for future ranking/outfit logic,
     * but we don't want every weak prediction to make
     * an item searchable.
     */
    private fun semanticTags(
        item: WardrobeItem
    ): List<SemanticTag> {

        return buildList {
            addAll(
                item.metadata.patterns
                    .take(3)
            )

            addAll(
                item.metadata.materials
                    .take(3)
            )

            addAll(
                item.metadata.styles
                    .take(3)
            )

            addAll(
                item.metadata.occasions
                    .take(3)
            )

            addAll(
                item.metadata.seasons
                    .take(3)
            )

            addAll(
                item.metadata.formalities
                    .take(3)
            )
        }
    }

    private fun canonicalizeQuery(
        query: String
    ): List<String> {

        var normalized =
            query
                .lowercase()
                .replace("-", " ")
                .replace("/", " ")
                .replace(
                    Regex(
                        "[^a-z0-9_\\s]"
                    ),
                    " "
                )
                .replace(
                    Regex("\\s+"),
                    " "
                )
                .trim()

        phraseAliases
            .entries
            .sortedByDescending {
                it.key.length
            }
            .forEach {
                    (phrase, replacement) ->

                normalized =
                    Regex(
                        "\\b${
                            Regex.escape(
                                phrase
                            )
                        }\\b"
                    ).replace(
                        normalized,
                        replacement
                    )
            }

        return normalized
            .split(
                Regex("\\s+")
            )
            .filter {
                it.isNotBlank()
            }
            .map { token ->
                tokenAliases[token]
                    ?: token
            }
            .distinct()
    }

    private val phraseAliases =
        mapOf(
            "t shirts" to "tshirt",
            "t shirt" to "tshirt",

            "tee shirts" to "tshirt",
            "tee shirt" to "tshirt",

            "vest tops" to "vest_top",
            "vest top" to "vest_top",

            "smart casual" to
                    "smart_casual",

            "animal print" to
                    "animal_print",

            "polka dots" to
                    "polka_dot",

            "polka dot" to
                    "polka_dot",

            "navy blue" to "navy",

            "high heels" to "heels",

            /*
             * Two search concepts:
             *
             * graphic + tshirt
             */
            "graphic tee" to
                    "graphic tshirt"
        )

    private val tokenAliases =
        mapOf(
            "tee" to "tshirt",
            "tees" to "tshirt",
            "tshirts" to "tshirt",

            "hoodies" to "hoodie",
            "jumpers" to "jumper",
            "cardigans" to "cardigan",

            "jackets" to "jacket",
            "coats" to "coat",
            "blazers" to "blazer",

            "pants" to "trousers",
            "pant" to "trousers",

            "jean" to "jeans",

            "trainer" to "trainers",
            "sneaker" to "trainers",
            "sneakers" to "trainers",

            "shoe" to "shoes",
            "boot" to "boots",
            "sandal" to "sandals",
            "heel" to "heels",

            "pyjama" to "pyjamas",
            "pajama" to "pyjamas",
            "pajamas" to "pyjamas",

            "gray" to "grey",

            "plain" to "solid",

            "fall" to "autumn",

            "outdoor" to "outdoors",

            "office" to "work"
        )
}