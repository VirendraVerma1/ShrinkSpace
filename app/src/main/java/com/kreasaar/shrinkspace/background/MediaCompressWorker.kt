package com.kreasaar.shrinkspace.background

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.kreasaar.shrinkspace.data.MediaItem
import com.kreasaar.shrinkspace.data.MediaRepository
import com.kreasaar.shrinkspace.utils.CompressionUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MediaCompressWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    @Inject
    lateinit var mediaRepository: MediaRepository

    @Inject
    lateinit var compressionUtils: CompressionUtils

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            // Implement media compression logic
            val mediaId = inputData.getLong("media_id", -1)
            val quality = inputData.getInt("quality", 80)
            
            if (mediaId == -1L) {
                return@withContext Result.failure()
            }
            
            val mediaItem = mediaRepository.getMediaById(mediaId)
            if (mediaItem == null) {
                return@withContext Result.failure()
            }
            
            val inputFile = java.io.File(mediaItem.uri.path ?: "")
            if (!inputFile.exists()) {
                return@withContext Result.failure()
            }
            
            val outputFile = java.io.File(inputFile.parent, "compressed_${inputFile.name}")
            val success = when (mediaItem.type) {
                "image" -> {
                    compressionUtils.compressImage(
                        mediaItem.uri,
                        outputFile,
                        quality
                    )
                }
                "video" -> {
                    compressionUtils.compressVideo(
                        mediaItem.uri,
                        outputFile,
                        quality
                    )
                }
                else -> false
            }
            
            if (success) {
                // Update the media item with compressed file info
                val compressedItem = mediaItem.copy(
                    uri = android.net.Uri.fromFile(outputFile),
                    size = outputFile.length()
                )
                mediaRepository.updateMedia(compressedItem)
                
                // Delete original file if compression was successful
                inputFile.delete()
                
                Result.success()
            } else {
                Result.failure()
            }
        } catch (e: Exception) {
            Result.failure()
        }
    }
} 