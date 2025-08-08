package com.kreasaar.shrinkspace.background

import android.content.Context
import android.provider.MediaStore
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.kreasaar.shrinkspace.data.MediaItem
import com.kreasaar.shrinkspace.data.MediaRepository
import com.kreasaar.shrinkspace.media.MediaAccessManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MediaScanWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    // Resolve dependencies manually to avoid field injection in Worker
    private val mediaRepository: MediaRepository by lazy {
        val db = com.kreasaar.shrinkspace.data.ShrinkSpaceDatabase.getDatabase(applicationContext)
        MediaRepository(db.mediaDao())
    }

    private val mediaAccessManager: MediaAccessManager by lazy {
        MediaAccessManager(applicationContext)
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            // Implement media scanning via abstraction
            val mediaItems = mediaAccessManager.queryMedia()
            mediaItems.forEach { mediaItem -> mediaRepository.insertMedia(mediaItem) }
            
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
} 