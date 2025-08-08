package com.kreasaar.shrinkspace.background

import android.content.Context
import android.provider.MediaStore
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.kreasaar.shrinkspace.data.MediaItem
import com.kreasaar.shrinkspace.data.MediaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MediaSyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val mediaRepository: MediaRepository by lazy {
        val db = com.kreasaar.shrinkspace.data.ShrinkSpaceDatabase.getDatabase(applicationContext)
        MediaRepository(db.mediaDao())
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            // Implement media sync logic
            val existingMedia = mediaRepository.getAllMedia()
            val existingIds = existingMedia.map { it.id }.toSet()
            
            val currentMediaIds = mutableSetOf<Long>()
            
            // Check for images
            val imageCursor = applicationContext.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                arrayOf(MediaStore.Images.Media._ID),
                null,
                null,
                null
            )
            
            imageCursor?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    currentMediaIds.add(id)
                }
            }
            
            // Check for videos
            val videoCursor = applicationContext.contentResolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                arrayOf(MediaStore.Video.Media._ID),
                null,
                null,
                null
            )
            
            videoCursor?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    currentMediaIds.add(id)
                }
            }
            
            // Find deleted media items
            val deletedIds = existingIds - currentMediaIds
            deletedIds.forEach { id ->
                val mediaItem = existingMedia.find { it.id == id }
                if (mediaItem != null) {
                    mediaRepository.deleteMedia(mediaItem)
                }
            }
            
            // Find new media items
            val newIds = currentMediaIds - existingIds
            newIds.forEach { id ->
                // Query for the new media item details
                val imageCursor = applicationContext.contentResolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    arrayOf(
                        MediaStore.Images.Media._ID,
                        MediaStore.Images.Media.DISPLAY_NAME,
                        MediaStore.Images.Media.SIZE,
                        MediaStore.Images.Media.DATE_ADDED,
                        MediaStore.Images.Media.DATA
                    ),
                    "${MediaStore.Images.Media._ID} = ?",
                    arrayOf(id.toString()),
                    null
                )
                
                imageCursor?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                        val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
                        val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
                        val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                        
                        val name = cursor.getString(nameColumn)
                        val size = cursor.getLong(sizeColumn)
                        val dateAdded = cursor.getLong(dateColumn)
                        val path = cursor.getString(pathColumn)
                        
                        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon()
                            .appendPath(id.toString())
                            .build()
                        
                        val mediaItem = MediaItem(
                            id = id,
                            name = name,
                            uri = uri,
                            type = "image",
                            size = size,
                            dateAdded = dateAdded,
                            path = path
                        )
                        mediaRepository.insertMedia(mediaItem)
                    }
                }
                
                // Check for videos if not found in images
                if (imageCursor?.count == 0) {
                    val videoCursor = applicationContext.contentResolver.query(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        arrayOf(
                            MediaStore.Video.Media._ID,
                            MediaStore.Video.Media.DISPLAY_NAME,
                            MediaStore.Video.Media.SIZE,
                            MediaStore.Video.Media.DATE_ADDED,
                            MediaStore.Video.Media.DATA
                        ),
                        "${MediaStore.Video.Media._ID} = ?",
                        arrayOf(id.toString()),
                        null
                    )
                    
                    videoCursor?.use { cursor ->
                        if (cursor.moveToFirst()) {
                            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
                            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
                            val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)
                            val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
                            
                            val name = cursor.getString(nameColumn)
                            val size = cursor.getLong(sizeColumn)
                            val dateAdded = cursor.getLong(dateColumn)
                            val path = cursor.getString(pathColumn)
                            
                            val uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI.buildUpon()
                                .appendPath(id.toString())
                                .build()
                            
                            val mediaItem = MediaItem(
                                id = id,
                                name = name,
                                uri = uri,
                                type = "video",
                                size = size,
                                dateAdded = dateAdded,
                                path = path
                            )
                            mediaRepository.insertMedia(mediaItem)
                        }
                    }
                }
            }
            
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
} 