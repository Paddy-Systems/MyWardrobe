package com.paddysystems.wearfolio.data.model

data class WardrobeEvent(
    val id: String,
    val name: String,
    val date: Long? = null,
    val imagePath: String? = null
)