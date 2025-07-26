package com.kreasaar.shrinkspace.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.kreasaar.shrinkspace.R
import com.kreasaar.shrinkspace.ui.viewmodels.CompressionViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CompressionOptionsFragment : Fragment() {
    private val viewModel: CompressionViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate compression options layout and show progress
        return inflater.inflate(R.layout.fragment_compression_options, container, false)
    }
    // Add onViewCreated logic to bind ViewModel to UI as needed
} 