package com.kreasaar.shrinkspace.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.kreasaar.shrinkspace.R
import com.kreasaar.shrinkspace.ui.viewmodels.PhotoPickerViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BestPhotoPickerFragment : Fragment() {
    private val viewModel: PhotoPickerViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate best photo picker layout and show carousel
        return inflater.inflate(R.layout.fragment_best_photo_picker, container, false)
    }
    // Add onViewCreated logic to bind ViewModel to UI as needed
} 