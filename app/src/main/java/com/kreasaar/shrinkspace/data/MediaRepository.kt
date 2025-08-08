package com.kreasaar.shrinkspace.data

class MediaRepository(private val mediaDao: MediaDao) {
    suspend fun getAllMedia() = mediaDao.getAll()
    suspend fun getMediaById(id: Long) = mediaDao.getById(id)
    suspend fun insertMedia(mediaItem: MediaItem) = mediaDao.insert(mediaItem)
    suspend fun updateMedia(mediaItem: MediaItem) = mediaDao.update(mediaItem)
    suspend fun deleteMedia(mediaItem: MediaItem) = mediaDao.delete(mediaItem)
} 