package com.insightra.app.network.model

import kotlinx.serialization.Serializable

@Serializable
data class VisionExtractResult(
    val brand: String,
    val name: String,
    val size: String
)

@Serializable
data class ComparisonAttributeRow(
    val attribute: String,
    val values: List<String>
)

@Serializable
data class ItemRating(
    val itemIndex: Int,
    val averageOutOf5: Double,
    val totalReviews: Int
)

@Serializable
data class ProsCons(
    val itemIndex: Int,
    val pros: List<String>,
    val cons: List<String>
)

@Serializable
data class Recommendation(
    val text: String
)

@Serializable
data class ComparisonResultPayload(
    val table: List<ComparisonAttributeRow>,
    val ratings: List<ItemRating>,
    val prosCons: List<ProsCons>,
    val recommendations: List<Recommendation>
)
