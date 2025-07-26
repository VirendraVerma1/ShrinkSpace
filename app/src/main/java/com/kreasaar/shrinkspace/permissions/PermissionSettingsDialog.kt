package com.kreasaar.shrinkspace.permissions

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import javax.inject.Inject

class PermissionSettingsDialog : DialogFragment() {
    
    @Inject
    lateinit var permissionManager: PermissionManager
    
    private var onSettingsOpened: (() -> Unit)? = null
    private var onCancelled: (() -> Unit)? = null
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle("Permissions Required")
            .setMessage("ShrinkSpace needs access to your media files to function properly. Please enable the required permissions in Settings.")
            .setPositiveButton("Open Settings") { _, _ ->
                // Open app settings
                openAppSettings()
                onSettingsOpened?.invoke()
            }
            .setNegativeButton("Cancel") { _, _ ->
                // Handle cancellation
                onCancelled?.invoke()
            }
            .setCancelable(false)
            .create()
    }
    
    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", requireContext().packageName, null)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
    }
    
    fun setCallbacks(
        onSettingsOpened: () -> Unit,
        onCancelled: () -> Unit
    ) {
        this.onSettingsOpened = onSettingsOpened
        this.onCancelled = onCancelled
    }
    
    companion object {
        const val TAG = "PermissionSettingsDialog"
    }
} 