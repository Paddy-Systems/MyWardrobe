package com.paddysystems.wearfolio.ml

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object WardrobeAnalysisService {
    suspend fun analyse(
        context: Context,
        imageUri: Uri
    ): FashionAnalysisResult = withContext(Dispatchers.IO) {
        val applicationContext = context.applicationContext

        FashionSigLipEncoder(applicationContext).use { encoder ->
            val embedding = encoder.encode(imageUri)

            val clothingMatcher = ClothingEmbeddingMatcher(applicationContext)
            val colourMatcher = ColourEmbeddingMatcher(applicationContext)
            val metadataAnalyzer = SemanticMetadataAnalyzer(applicationContext)

            FashionAnalysisResult(
                embedding = embedding,
                clothingMatches = clothingMatcher.topMatches(embedding, 5),
                colourMatches = colourMatcher.topMatches(embedding, 5),
                metadata = metadataAnalyzer.analyze(embedding)
            )
        }
    }
}
