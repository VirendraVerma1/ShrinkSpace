package com.kreasaar.shrinkspace.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kreasaar.shrinkspace.permissions.PermissionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PermissionViewModel @Inject constructor(
    private val permissionManager: PermissionManager
) : ViewModel() {

    private val _permissionsGranted = MutableLiveData<Boolean>()
    val permissionsGranted: LiveData<Boolean> = _permissionsGranted

    private val _missingPermissions = MutableLiveData<Array<String>>()
    val missingPermissions: LiveData<Array<String>> = _missingPermissions

    fun checkPermissionStatus() {
        // Check current permission status
        val hasAllPermissions = permissionManager.hasAllPermissions()
        _permissionsGranted.value = hasAllPermissions
        
        if (!hasAllPermissions) {
            val missing = permissionManager.getMissingPermissions()
            _missingPermissions.value = missing
        }
    }

    fun getRequiredPermissions(): Array<String> {
        return permissionManager.getRequiredPermissions()
    }

    fun requestPermissions() {
        // Request permissions
        // This will be handled by the Fragment/Activity
        // The ViewModel just provides the data
        checkPermissionStatus()
    }

    fun shouldShowRationale(activity: android.app.Activity): Boolean {
        return permissionManager.shouldShowRationale(activity)
    }
} 