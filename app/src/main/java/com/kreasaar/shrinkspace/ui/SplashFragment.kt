package com.kreasaar.shrinkspace.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.kreasaar.shrinkspace.R
import com.kreasaar.shrinkspace.ui.viewmodels.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.navigation.fragment.findNavController
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import android.content.Context

@AndroidEntryPoint
class SplashFragment : Fragment() {
    
    private val viewModel: SplashViewModel by viewModels()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate splash layout and show branding/loading
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Observe view model state
        viewModel.isInitialized.observe(viewLifecycleOwner) { isInitialized ->
            if (isInitialized) {
                navigateToNextScreen()
            }
        }
        
        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                // Show error state
                showError(it)
            }
        }
        
        // Start initialization
        viewModel.initializeApp()
        // Kick off a background delta sync if permissions exist
        if (viewModel.hasPermissions()) {
            // First-run full scan
            val prefs = requireContext().getSharedPreferences("app_init", Context.MODE_PRIVATE)
            val isInitialized = prefs.getBoolean("is_initialized", false)
            if (!isInitialized) {
                val scanWork = OneTimeWorkRequestBuilder<com.kreasaar.shrinkspace.background.MediaScanWorker>().build()
                WorkManager.getInstance(requireContext())
                    .enqueueUniqueWork("media_full_scan", ExistingWorkPolicy.KEEP, scanWork)
                prefs.edit().putBoolean("is_initialized", true).apply()
            }
            val work = OneTimeWorkRequestBuilder<com.kreasaar.shrinkspace.background.MediaSyncWorker>().build()
            WorkManager.getInstance(requireContext())
                .enqueueUniqueWork("media_delta_sync", ExistingWorkPolicy.KEEP, work)
        }
    }
    
    private fun navigateToNextScreen() {
        // Navigate to permissions or home based on permission status
        val navController = findNavController()
        if (viewModel.hasPermissions()) {
            navController.navigate(R.id.action_splash_to_home)
        } else {
            navController.navigate(R.id.action_splash_to_permissions)
        }
    }
    
    private fun showError(error: String) {
        // Show error dialog or message
        // Implementation depends on your error handling strategy
    }
} 