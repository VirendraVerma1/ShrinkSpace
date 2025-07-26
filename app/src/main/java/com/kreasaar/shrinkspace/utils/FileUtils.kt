package com.kreasaar.shrinkspace.utils

import android.content.Context
import android.net.Uri
import java.io.File

class FileUtils(private val context: Context) {
    
    fun getFileExtension(file: File): String {
        val name = file.name
        val lastDot = name.lastIndexOf('.')
        return if (lastDot > 0) name.substring(lastDot + 1) else ""
    }

    fun isImageFile(file: File): Boolean {
        val extension = getFileExtension(file).lowercase()
        return extension in listOf("jpg", "jpeg", "png", "gif", "bmp", "webp")
    }

    fun isVideoFile(file: File): Boolean {
        val extension = getFileExtension(file).lowercase()
        return extension in listOf("mp4", "avi", "mov", "wmv", "flv", "mkv", "webm")
    }

    fun getFileSize(file: File): Long {
        return if (file.exists()) file.length() else 0
    }

    fun deleteFile(file: File): Boolean {
        return if (file.exists()) file.delete() else false
    }

    fun createBackupFile(originalFile: File): File? {
        return try {
            val backupDir = File(context.filesDir, "backups")
            if (!backupDir.exists()) {
                backupDir.mkdirs()
            }
            val backupFile = File(backupDir, "backup_${originalFile.name}")
            originalFile.copyTo(backupFile, overwrite = true)
            backupFile
        } catch (e: Exception) {
            null
        }
    }
} 