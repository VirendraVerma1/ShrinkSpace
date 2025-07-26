package com.kreasaar.shrinkspace.ui.viewmodels

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kreasaar.shrinkspace.permissions.PermissionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class ErrorStateViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val permissionManager: PermissionManager
) : ViewModel() {

    private val _errorType = MutableLiveData<String>()
    val errorType: LiveData<String> = _errorType

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _canRetry = MutableLiveData<Boolean>()
    val canRetry: LiveData<Boolean> = _canRetry

    private val _retryAction = MutableLiveData<() -> Unit>()
    val retryAction: LiveData<() -> Unit> = _retryAction

    fun setError(errorType: String, message: String, canRetry: Boolean = true, retryAction: (() -> Unit)? = null) {
        _errorType.value = errorType
        _errorMessage.value = message
        _canRetry.value = canRetry
        _retryAction.value = retryAction
    }

    fun retry() {
        // Implement retry logic based on error type
        when (_errorType.value) {
            "permission" -> {
                checkPermissionStatus()
            }
            "storage" -> {
                // Retry storage access
                _retryAction.value?.invoke()
            }
            "network" -> {
                // Retry network operation
                _retryAction.value?.invoke()
            }
            "compression" -> {
                // Retry compression operation
                _retryAction.value?.invoke()
            }
            else -> {
                // Generic retry
                _retryAction.value?.invoke()
            }
        }
    }

    fun openAppSettings() {
        // Open app settings
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    fun checkPermissionStatus(): Boolean {
        // Check permission status
        return permissionManager.hasAllPermissions()
    }

    fun getPermissionRationale(): String {
        return permissionManager.getPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    fun getMissingPermissions(): Array<String> {
        return permissionManager.getMissingPermissions()
    }

    fun shouldShowRationale(activity: android.app.Activity): Boolean {
        return permissionManager.shouldShowRationale(activity)
    }

    fun clearError() {
        _errorType.value = null
        _errorMessage.value = null
        _canRetry.value = false
        _retryAction.value = null
    }
} 