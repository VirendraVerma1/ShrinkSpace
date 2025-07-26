package com.kreasaar.shrinkspace.utils

import android.content.Context
import android.os.Environment
import java.io.File

class StorageUtils(private val context: Context) {
    
    fun getTotalStorage(): Long {
        val stat = StatFs(Environment.getExternalStorageDirectory().path)
        return stat.totalBytes
    }

    fun getAvailableStorage(): Long {
        val stat = StatFs(Environment.getExternalStorageDirectory().path)
        return stat.availableBytes
    }

    fun getUsedStorage(): Long {
        return getTotalStorage() - getAvailableStorage()
    }

    fun formatFileSize(bytes: Long): String {
        return when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> "${bytes / 1024} KB"
            bytes < 1024 * 1024 * 1024 -> "${bytes / (1024 * 1024)} MB"
            else -> "${bytes / (1024 * 1024 * 1024)} GB"
        }
    }

    fun getStoragePercentage(): Float {
        return (getUsedStorage().toFloat() / getTotalStorage().toFloat()) * 100
    }
} 