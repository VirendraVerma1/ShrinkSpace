package com.kreasaar.shrinkspace.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.kreasaar.shrinkspace.R
import com.kreasaar.shrinkspace.ui.viewmodels.SmartSuggestionsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SmartSuggestionsFragment : Fragment() {
    private val viewModel: SmartSuggestionsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate smart suggestions layout and show flagged items
        return inflater.inflate(R.layout.fragment_smart_suggestions, container, false)
    }
    // Add onViewCreated logic to bind ViewModel to UI as needed
} 