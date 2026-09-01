package com.paddysystems.mywardrobe.domain.additem

import android.content.Context
import android.net.Uri
import com.paddysystems.mywardrobe.ml.ClothingEmbeddingMatcher
import com.paddysystems.mywardrobe.ml.ColourEmbeddingMatcher
import com.paddysystems.mywardrobe.ml.FashionAnalysisResult
import com.paddysystems.mywardrobe.ml.FashionSigLipEncoder
import com.paddysystems.mywardrobe.ml.SemanticMetadataAnalyzer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FashionItemAnalyzer(private val context: Context) {
    suspend fun analyze(imageUri: Uri): FashionAnalysisResult = withContext(Dispatchers.IO) {
        val embedding = FashionSigLipEncoder(context).encode(imageUri)
        FashionAnalysisResult(
            embedding = embedding,
            clothingMatches = ClothingEmbeddingMatcher(context).topMatches(embedding, 5),
            colourMatches = ColourEmbeddingMatcher(context).topMatches(embedding, 5),
            metadata = SemanticMetadataAnalyzer(context).analyze(embedding)
        )
    }
}
