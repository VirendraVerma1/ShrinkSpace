package com.kreasaar.shrinkspace.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.kreasaar.shrinkspace.R
import com.kreasaar.shrinkspace.ui.viewmodels.PermissionViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PermissionFragment : Fragment() {
    private val viewModel: PermissionViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate permission layout and handle permission requests
        return inflater.inflate(R.layout.fragment_permissions, container, false)
    }
    // Add onViewCreated logic to bind ViewModel to UI as needed
} 