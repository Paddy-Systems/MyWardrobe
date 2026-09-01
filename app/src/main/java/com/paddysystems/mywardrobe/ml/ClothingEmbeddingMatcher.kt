package com.paddysystems.mywardrobe.ml

import android.content.Context
import org.json.JSONObject
import kotlin.math.sqrt

data class ClothingMatch(
    val id: String,
    val similarity: Float
)

class ClothingEmbeddingMatcher(
    private val context: Context
) {

    private val referenceEmbeddings: Map<String, FloatArray> by lazy {
        loadReferenceEmbeddings()
    }

    fun topMatches(
        imageEmbedding: FloatArray,
        limit: Int = 5
    ): List<ClothingMatch> {
        return referenceEmbeddings
            .map { (id, referenceEmbedding) ->
                ClothingMatch(
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

    private fun loadReferenceEmbeddings(): Map<String, FloatArray> {
        val jsonText = context.assets
            .open("models/clothing_embeddings.json")
            .bufferedReader()
            .use { reader ->
                reader.readText()
            }

        val json = JSONObject(jsonText)

        return json.keys()
            .asSequence()
            .associateWith { id ->
                val values = json.getJSONArray(id)

                FloatArray(values.length()) { index ->
                    values.getDouble(index).toFloat()
                }
            }
    }

    private fun cosineSimilarity(
        first: FloatArray,
        second: FloatArray
    ): Float {
        require(first.size == second.size) {
            "Embedding sizes do not match"
        }

        var dotProduct = 0.0
        var firstMagnitude = 0.0
        var secondMagnitude = 0.0

        for (index in first.indices) {
            val firstValue = first[index].toDouble()
            val secondValue = second[index].toDouble()

            dotProduct += firstValue * secondValue

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