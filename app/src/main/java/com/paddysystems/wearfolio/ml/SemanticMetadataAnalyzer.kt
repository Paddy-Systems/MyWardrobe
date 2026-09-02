package com.paddysystems.wearfolio.ml

import android.content.Context
import com.paddysystems.wearfolio.data.model.WardrobeMetadata

class SemanticMetadataAnalyzer(
    context: Context
) {

    private val matcher =
        SemanticEmbeddingMatcher(
            context.applicationContext
        )

    fun analyze(
        imageEmbedding: FloatArray
    ): WardrobeMetadata {

        return WardrobeMetadata(
            patterns =
                matcher.topMatches(
                    category = "patterns",
                    imageEmbedding =
                        imageEmbedding,
                    limit = 5
                ),

            materials =
                matcher.topMatches(
                    category = "materials",
                    imageEmbedding =
                        imageEmbedding,
                    limit = 5
                ),

            styles =
                matcher.topMatches(
                    category = "styles",
                    imageEmbedding =
                        imageEmbedding,
                    limit = 5
                ),

            occasions =
                matcher.topMatches(
                    category = "occasions",
                    imageEmbedding =
                        imageEmbedding,
                    limit = 5
                ),

            seasons =
                matcher.topMatches(
                    category = "seasons",
                    imageEmbedding =
                        imageEmbedding,
                    limit = 4
                ),

            formalities =
                matcher.topMatches(
                    category = "formalities",
                    imageEmbedding =
                        imageEmbedding,
                    limit = 3
                )
        )
    }
}