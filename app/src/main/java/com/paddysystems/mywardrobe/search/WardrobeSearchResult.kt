package com.paddysystems.mywardrobe.search

import com.paddysystems.mywardrobe.data.model.WardrobeItem

data class WardrobeSearchResult(
    val item: WardrobeItem,
    val score: Float
)
