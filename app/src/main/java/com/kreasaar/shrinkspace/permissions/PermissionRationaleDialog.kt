package com.kreasaar.shrinkspace.permissions

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import javax.inject.Inject

class PermissionRationaleDialog : DialogFragment() {
    
    @Inject
    lateinit var permissionManager: PermissionManager
    
    private var onPermissionRequested: (() -> Unit)? = null
    private var onPermissionDenied: (() -> Unit)? = null
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle("Permission Required")
            .setMessage("ShrinkSpace needs access to your media files to help you organize and clean up storage space. Please grant the required permissions to continue.")
            .setPositiveButton("Grant Permissions") { _, _ ->
                // Request permissions
                onPermissionRequested?.invoke()
            }
            .setNegativeButton("Cancel") { _, _ ->
                // Handle permission denial
                onPermissionDenied?.invoke()
            }
            .setCancelable(false)
            .create()
    }
    
    fun setCallbacks(
        onPermissionRequested: () -> Unit,
        onPermissionDenied: () -> Unit
    ) {
        this.onPermissionRequested = onPermissionRequested
        this.onPermissionDenied = onPermissionDenied
    }
    
    companion object {
        const val TAG = "PermissionRationaleDialog"
    }
} 