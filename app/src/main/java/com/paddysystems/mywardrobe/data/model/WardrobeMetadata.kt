package com.paddysystems.mywardrobe.data.model

data class WardrobeMetadata(
    val patterns: List<SemanticTag> = emptyList(),
    val materials: List<SemanticTag> = emptyList(),
    val styles: List<SemanticTag> = emptyList(),
    val occasions: List<SemanticTag> = emptyList(),
    val seasons: List<SemanticTag> = emptyList(),
    val formalities: List<SemanticTag> = emptyList()
)