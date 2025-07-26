package com.kreasaar.shrinkspace.data

import androidx.room.*

@Dao
interface JobDao {
    @Query("SELECT * FROM job_states")
    suspend fun getAll(): List<JobState>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(jobState: JobState): Long

    @Update
    suspend fun update(jobState: JobState)

    @Delete
    suspend fun delete(jobState: JobState)
} 