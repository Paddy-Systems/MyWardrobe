package com.paddysystems.mywardrobe.ml

import com.paddysystems.mywardrobe.data.model.WardrobeMetadata

data class FashionAnalysisResult(
    val embedding: FloatArray,
    val clothingMatches:
    List<ClothingMatch>,
    val colourMatches:
    List<ColourMatch>,
    val metadata:
    WardrobeMetadata
)