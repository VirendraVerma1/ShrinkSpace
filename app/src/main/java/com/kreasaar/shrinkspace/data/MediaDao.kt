package com.kreasaar.shrinkspace.data

import androidx.room.*

@Dao
interface MediaDao {
    @Query("SELECT * FROM media_items")
    suspend fun getAll(): List<MediaItem>

    @Query("SELECT * FROM media_items WHERE id = :id")
    suspend fun getById(id: Long): MediaItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(mediaItem: MediaItem): Long

    @Update
    suspend fun update(mediaItem: MediaItem)

    @Delete
    suspend fun delete(mediaItem: MediaItem)
} 