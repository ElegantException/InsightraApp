package com.insightra.app.network.model

data class VisionExtractResult(
    val brand: String,
    val name: String,
    val size: String
)

data class ComparisonAttributeRow(
    val attribute: String,
    val values: List<String>
)

data class ItemRating(
    val itemIndex: Int,
    val averageOutOf5: Double,
    val totalReviews: Int
)

data class ProsCons(
    val itemIndex: Int,
    val pros: List<String>,
    val cons: List<String>
)

data class Recommendation(
    val text: String
)

data class ComparisonResultPayload(
    val table: List<ComparisonAttributeRow>,
    val ratings: List<ItemRating>,
    val prosCons: List<ProsCons>,
    val recommendations: List<Recommendation>
)
