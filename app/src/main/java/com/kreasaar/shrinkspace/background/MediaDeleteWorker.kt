package com.kreasaar.shrinkspace.background

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.kreasaar.shrinkspace.data.MediaItem
import com.kreasaar.shrinkspace.data.MediaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MediaDeleteWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    @Inject
    lateinit var mediaRepository: MediaRepository

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            // Implement media deletion logic
            val mediaId = inputData.getLong("media_id", -1)
            
            if (mediaId == -1L) {
                return@withContext Result.failure()
            }
            
            val mediaItem = mediaRepository.getMediaById(mediaId)
            if (mediaItem == null) {
                return@withContext Result.failure()
            }
            
            val file = java.io.File(mediaItem.uri.path ?: "")
            if (file.exists()) {
                val deleted = file.delete()
                if (deleted) {
                    // Remove from repository
                    mediaRepository.deleteMedia(mediaItem)
                    Result.success()
                } else {
                    Result.failure()
                }
            } else {
                // File doesn't exist, just remove from repository
                mediaRepository.deleteMedia(mediaItem)
                Result.success()
            }
        } catch (e: Exception) {
            Result.failure()
        }
    }
} 