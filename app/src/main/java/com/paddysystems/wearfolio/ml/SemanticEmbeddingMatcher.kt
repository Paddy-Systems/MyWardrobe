package com.paddysystems.wearfolio.ml

import android.content.Context
import com.paddysystems.wearfolio.data.model.SemanticTag
import org.json.JSONObject
import kotlin.math.sqrt

class SemanticEmbeddingMatcher(
    private val context: Context
) {

    private val referenceEmbeddings:
            Map<String, Map<String, FloatArray>>
            by lazy {
                loadReferenceEmbeddings()
            }

    fun topMatches(
        category: String,
        imageEmbedding: FloatArray,
        limit: Int = 5
    ): List<SemanticTag> {

        val categoryEmbeddings =
            referenceEmbeddings[category]
                ?: error(
                    "Unknown semantic category: $category"
                )

        return categoryEmbeddings
            .map { (id, referenceEmbedding) ->
                SemanticTag(
                    id = id,
                    similarity = cosineSimilarity(
                        imageEmbedding,
                        referenceEmbedding
                    )
                )
            }
            .sortedByDescending {
                it.similarity
            }
            .take(limit)
    }

    private fun loadReferenceEmbeddings():
            Map<String, Map<String, FloatArray>> {

        val jsonText =
            context.assets
                .open(
                    "models/semantic_embeddings.json"
                )
                .bufferedReader()
                .use {
                    it.readText()
                }

        val root =
            JSONObject(jsonText)

        return root.keys()
            .asSequence()
            .associateWith { category ->

                val categoryJson =
                    root.getJSONObject(category)

                categoryJson.keys()
                    .asSequence()
                    .associateWith { id ->

                        val values =
                            categoryJson.getJSONArray(
                                id
                            )

                        FloatArray(
                            values.length()
                        ) { index ->
                            values
                                .getDouble(index)
                                .toFloat()
                        }
                    }
            }
    }

    private fun cosineSimilarity(
        first: FloatArray,
        second: FloatArray
    ): Float {

        require(
            first.size == second.size
        ) {
            "Embedding sizes do not match"
        }

        var dotProduct = 0.0
        var firstMagnitude = 0.0
        var secondMagnitude = 0.0

        for (index in first.indices) {
            val firstValue =
                first[index].toDouble()

            val secondValue =
                second[index].toDouble()

            dotProduct +=
                firstValue * secondValue

            firstMagnitude +=
                firstValue * firstValue

            secondMagnitude +=
                secondValue * secondValue
        }

        if (
            firstMagnitude == 0.0 ||
            secondMagnitude == 0.0
        ) {
            return 0f
        }

        return (
                dotProduct /
                        (
                                sqrt(firstMagnitude) *
                                        sqrt(secondMagnitude)
                                )
                ).toFloat()
    }
}