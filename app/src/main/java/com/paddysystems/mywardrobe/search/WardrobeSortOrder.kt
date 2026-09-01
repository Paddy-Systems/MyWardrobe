package com.paddysystems.mywardrobe.search

enum class WardrobeSortOrder(
    val label: String
) {
    AUTO("Default"),
    NEWEST("Newest first"),
    OLDEST("Oldest first"),
    TYPE("Clothing type"),
    COLOUR("Colour")
}