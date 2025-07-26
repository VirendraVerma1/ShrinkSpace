package com.kreasaar.shrinkspace.media

import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class MediaAccessManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun queryMedia(): List<MediaItem> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Use MediaStore and ContentResolver for Android 10+
            queryMediaStore()
        } else {
            // Use direct file access for Android 6-9
            queryFilesDirectly()
        }
    }

    private fun queryMediaStore(): List<MediaItem> {
        val mediaItems = mutableListOf<MediaItem>()
        
        // Query images
        val imageCursor = context.contentResolver.query(
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
        
        // Query videos
        val videoCursor = context.contentResolver.query(
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
        
        return mediaItems
    }

    private fun queryFilesDirectly(): List<MediaItem> {
        val mediaItems = mutableListOf<MediaItem>()
        val externalStorageDir = context.getExternalFilesDir(null)
        
        if (externalStorageDir != null) {
            scanDirectory(externalStorageDir, mediaItems)
        }
        
        return mediaItems
    }

    private fun scanDirectory(directory: java.io.File, mediaItems: MutableList<MediaItem>) {
        val files = directory.listFiles()
        files?.forEach { file ->
            if (file.isDirectory) {
                scanDirectory(file, mediaItems)
            } else {
                val extension = file.extension.lowercase()
                when (extension) {
                    "jpg", "jpeg", "png", "gif", "bmp" -> {
                        val mediaItem = MediaItem(
                            id = file.hashCode().toLong(),
                            name = file.name,
                            uri = Uri.fromFile(file),
                            type = "image",
                            size = file.length(),
                            dateAdded = file.lastModified(),
                            path = file.absolutePath
                        )
                        mediaItems.add(mediaItem)
                    }
                    "mp4", "avi", "mov", "mkv", "wmv" -> {
                        val mediaItem = MediaItem(
                            id = file.hashCode().toLong(),
                            name = file.name,
                            uri = Uri.fromFile(file),
                            type = "video",
                            size = file.length(),
                            dateAdded = file.lastModified(),
                            path = file.absolutePath
                        )
                        mediaItems.add(mediaItem)
                    }
                }
            }
        }
    }

    fun updateMedia(mediaItem: MediaItem): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Update via ContentResolver for Android 10+
            updateViaContentResolver(mediaItem)
        } else {
            // Update file or DB directly for Android 6-9
            updateFileDirectly(mediaItem)
        }
    }

    private fun updateViaContentResolver(mediaItem: MediaItem): Boolean {
        return try {
            val values = android.content.ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, mediaItem.name)
                put(MediaStore.Images.Media.SIZE, mediaItem.size)
            }
            
            val uri = if (mediaItem.type == "image") {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            } else {
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            }
            
            val updatedRows = context.contentResolver.update(
                uri,
                values,
                "${MediaStore.Images.Media._ID} = ?",
                arrayOf(mediaItem.id.toString())
            )
            
            updatedRows > 0
        } catch (e: Exception) {
            false
        }
    }

    private fun updateFileDirectly(mediaItem: MediaItem): Boolean {
        return try {
            val file = java.io.File(mediaItem.uri.path ?: "")
            if (file.exists()) {
                // Update file metadata if needed
                file.setLastModified(mediaItem.dateAdded)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    data class MediaItem(
        val id: Long,
        val name: String,
        val uri: Uri,
        val type: String,
        val size: Long,
        val dateAdded: Long,
        val path: String
    )
} 