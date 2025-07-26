package com.kreasaar.shrinkspace.data

import androidx.room.*

@Dao
interface LogDao {
    @Query("SELECT * FROM log_entries ORDER BY timestamp DESC")
    suspend fun getAll(): List<LogEntry>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(logEntry: LogEntry): Long

    @Delete
    suspend fun delete(logEntry: LogEntry)
} 