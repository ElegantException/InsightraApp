package com.insightra.app.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface ComparisonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComparison(entity: ComparisonEntity): Long

    @Query("SELECT * FROM comparisons ORDER BY createdAt DESC LIMIT 2")
    fun recent(): Flow<List<ComparisonEntity>>

    @Query("SELECT * FROM comparisons WHERE id = :id LIMIT 1")
    suspend fun get(id: Long): ComparisonEntity?
}

@Dao
interface ItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(entity: ItemEntity): Long

    @Query("SELECT * FROM items WHERE comparisonId = :comparisonId")
    suspend fun itemsForComparison(comparisonId: Long): List<ItemEntity>
}

@Dao
interface ResultDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResult(entity: ComparisonResultEntity)

    @Query("SELECT * FROM results WHERE comparisonId = :comparisonId LIMIT 1")
    suspend fun resultForComparison(comparisonId: Long): ComparisonResultEntity?
}
