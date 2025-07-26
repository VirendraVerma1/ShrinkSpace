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

    @Inject
    lateinit var mediaRepository: MediaRepository

    @Inject
    lateinit var mediaAccessManager: MediaAccessManager

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            // Implement media scanning logic
            val mediaItems = mutableListOf<MediaItem>()
            
            // Scan for images
            val imageCursor = applicationContext.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                arrayOf(
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.SIZE,
                    MediaStore.Images.Media.DATE_ADDED,
                    MediaStore.Images.Media.DATA
                ),
                null,
                null,
                "${MediaStore.Images.Media.DATE_ADDED} DESC"
            )
            
            imageCursor?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
                val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
                val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
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
                    mediaItems.add(mediaItem)
                }
            }
            
            // Scan for videos
            val videoCursor = applicationContext.contentResolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                arrayOf(
                    MediaStore.Video.Media._ID,
                    MediaStore.Video.Media.DISPLAY_NAME,
                    MediaStore.Video.Media.SIZE,
                    MediaStore.Video.Media.DATE_ADDED,
                    MediaStore.Video.Media.DATA
                ),
                null,
                null,
                "${MediaStore.Video.Media.DATE_ADDED} DESC"
            )
            
            videoCursor?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
                val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
                val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
                val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)
                val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
                
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
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
                    mediaItems.add(mediaItem)
                }
            }
            
            // Insert all media items into repository
            mediaItems.forEach { mediaItem ->
                mediaRepository.insertMedia(mediaItem)
            }
            
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
} 