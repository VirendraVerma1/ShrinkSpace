package com.kreasaar.shrinkspace.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.kreasaar.shrinkspace.R
import com.kreasaar.shrinkspace.viewmodel.HomeViewModel
import com.kreasaar.shrinkspace.ui.components.StorageInfoView
import com.kreasaar.shrinkspace.ui.components.ProgressBarView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private val viewModel: HomeViewModel by viewModels()
    private var storageInfoView: StorageInfoView? = null
    private var syncProgressView: ProgressBarView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate home layout and show storage summary
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        storageInfoView = view.findViewById(R.id.storageInfoView)
        syncProgressView = view.findViewById(R.id.syncProgress)

        view.findViewById<android.view.View>(R.id.openGalleryButton)?.setOnClickListener {
            findNavController().navigate(R.id.galleryFragment)
        }
        view.findViewById<android.view.View>(R.id.openSmartSuggestionsButton)?.setOnClickListener {
            findNavController().navigate(R.id.smartSuggestionsFragment)
        }
        view.findViewById<android.view.View>(R.id.openSettingsButton)?.setOnClickListener {
            findNavController().navigate(R.id.settingsFragment)
        }

        viewModel.storageInfo.observe(viewLifecycleOwner) { info ->
            val mediaCount = viewModel.mediaCount.value ?: 0
            storageInfoView?.updateStorageInfo(
                usedStorage = info.usedSpace,
                totalStorage = info.totalSpace,
                mediaCount = mediaCount,
                animate = true
            )
        }

        viewModel.mediaCount.observe(viewLifecycleOwner) { count ->
            val info = viewModel.storageInfo.value
            if (info != null) {
                storageInfoView?.updateStorageInfo(
                    usedStorage = info.usedSpace,
                    totalStorage = info.totalSpace,
                    mediaCount = count,
                    animate = false
                )
            }
        }
    }
} 