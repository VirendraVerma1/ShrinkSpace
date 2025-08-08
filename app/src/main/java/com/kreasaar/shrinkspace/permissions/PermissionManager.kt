package com.kreasaar.shrinkspace.permissions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PermissionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    companion object {
        fun requiredPermissionsForDevice(): Array<String> {
            return when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO
                )
                else -> arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            }
        }
    }
    
    fun hasAllPermissions(): Boolean {
        return requiredPermissionsForDevice().all { permission ->
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }
    }
    
    fun getRequiredPermissions(): Array<String> = requiredPermissionsForDevice()
    
    fun shouldShowRationale(activity: android.app.Activity): Boolean {
        return getRequiredPermissions().any { permission ->
            activity.shouldShowRequestPermissionRationale(permission)
        }
    }
    
    fun getMissingPermissions(): Array<String> {
        return getRequiredPermissions().filter { permission ->
            ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()
    }
    
    fun hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }
    
    fun getPermissionStatus(permission: String): PermissionStatus {
        return when (ContextCompat.checkSelfPermission(context, permission)) {
            PackageManager.PERMISSION_GRANTED -> PermissionStatus.GRANTED
            PackageManager.PERMISSION_DENIED -> PermissionStatus.DENIED
            else -> PermissionStatus.UNKNOWN
        }
    }
    
    fun getPermissionRationale(permission: String): String {
        return when (permission) {
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_EXTERNAL_STORAGE -> 
                "ShrinkSpace needs access to your media files to analyze and help you organize them."
            Manifest.permission.READ_MEDIA_VIDEO ->
                "ShrinkSpace needs access to your videos to analyze and help you organize them."
            Manifest.permission.WRITE_EXTERNAL_STORAGE -> 
                "ShrinkSpace needs write access to compress and optimize your media files."
            else -> "This permission is required for the app to function properly."
        }
    }
    
    enum class PermissionStatus {
        GRANTED,
        DENIED,
        UNKNOWN
    }
} 