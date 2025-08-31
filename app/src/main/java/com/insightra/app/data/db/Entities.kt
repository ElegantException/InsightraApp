package com.insightra.app.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "comparisons")
data class ComparisonEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val createdAt: Long,
    val itemCount: Int
)

@Entity(tableName = "items")
data class ItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val comparisonId: Long,
    val imageUri: String,
    val brand: String,
    val name: String,
    val size: String
)

@Entity(tableName = "results")
data class ComparisonResultEntity(
    @PrimaryKey val comparisonId: Long,
    val tableJson: String,
    val ratingsJson: String,
    val prosConsJson: String,
    val recommendationsJson: String
)
