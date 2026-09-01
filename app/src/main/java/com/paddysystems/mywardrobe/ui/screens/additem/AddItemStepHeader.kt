package com.paddysystems.mywardrobe.ui.screens.additem

val AddItemStep.headerEyebrow: String
    get() = when (this) {
        AddItemStep.IMAGE -> "A new piece"
        AddItemStep.ANALYSING -> "Smart wardrobe"
        AddItemStep.DETAILS -> "Review & refine"
    }

val AddItemStep.headerTitle: String
    get() = when (this) {
        AddItemStep.IMAGE -> "Add to wardrobe"
        AddItemStep.ANALYSING -> "Reading your piece"
        AddItemStep.DETAILS -> "The finishing touches"
    }

val AddItemStep.headerSubtitle: String
    get() = when (this) {
        AddItemStep.IMAGE -> "Choose a clear, well-lit photo. We’ll take care of the details."
        AddItemStep.ANALYSING -> "Identifying the garment, colour and best ways to style it."
        AddItemStep.DETAILS -> "Check our suggestions before saving it to your collection."
    }
