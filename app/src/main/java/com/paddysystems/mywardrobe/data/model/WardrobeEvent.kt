package com.paddysystems.mywardrobe.data.model

data class WardrobeEvent(
    val id: String,
    val name: String,
    val date: Long? = null,
    val imagePath: String? = null
)