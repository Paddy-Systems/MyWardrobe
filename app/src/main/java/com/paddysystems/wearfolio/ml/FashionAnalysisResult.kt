package com.paddysystems.wearfolio.ml

import com.paddysystems.wearfolio.data.model.WardrobeMetadata

data class FashionAnalysisResult(
    val embedding: FloatArray,
    val clothingMatches:
    List<ClothingMatch>,
    val colourMatches:
    List<ColourMatch>,
    val metadata:
    WardrobeMetadata
)