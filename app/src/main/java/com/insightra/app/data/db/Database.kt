package com.insightra.app.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ComparisonEntity::class, ItemEntity::class, ComparisonResultEntity::class],
    version = 1
)
abstract class InsightraDatabase : RoomDatabase() {
    abstract fun comparisonDao(): ComparisonDao
    abstract fun itemDao(): ItemDao
    abstract fun resultDao(): ResultDao
}
