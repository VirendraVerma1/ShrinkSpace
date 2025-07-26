package com.kreasaar.shrinkspace.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.kreasaar.shrinkspace.R
import com.kreasaar.shrinkspace.ui.viewmodels.ActivityLogViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ActivityLogFragment : Fragment() {
    private val viewModel: ActivityLogViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate activity log layout and show log list
        return inflater.inflate(R.layout.fragment_activity_log, container, false)
    }
    // Add onViewCreated logic to bind ViewModel to UI as needed
} 