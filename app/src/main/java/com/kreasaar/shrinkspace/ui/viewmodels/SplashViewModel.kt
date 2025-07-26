package com.kreasaar.shrinkspace.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kreasaar.shrinkspace.permissions.PermissionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val permissionManager: PermissionManager
) : ViewModel() {

    private val _isInitialized = MutableLiveData<Boolean>()
    val isInitialized: LiveData<Boolean> = _isInitialized

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun initializeApp() {
        viewModelScope.launch {
            try {
                // Perform app initialization tasks
                delay(2000) // Simulate initialization time
                
                // Check permissions and other initialization tasks
                val hasPermissions = permissionManager.hasAllPermissions()
                
                _isInitialized.value = true
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun hasPermissions(): Boolean {
        return permissionManager.hasAllPermissions()
    }
} 