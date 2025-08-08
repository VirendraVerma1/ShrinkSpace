package com.kreasaar.shrinkspace.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.kreasaar.shrinkspace.R
import com.kreasaar.shrinkspace.ui.viewmodels.PermissionViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PermissionFragment : Fragment() {
    private val viewModel: PermissionViewModel by viewModels()
    private val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        val allGranted = results.values.all { it }
        if (allGranted) {
            findNavController().navigate(R.id.homeFragment)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate permission layout and handle permission requests
        return inflater.inflate(R.layout.fragment_permissions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Immediately request permissions or show rationale
        view.findViewById<android.view.View>(R.id.requestPermissionsButton)?.setOnClickListener {
            val missing = viewModel.missingPermissions.value ?: viewModel.getRequiredPermissions()
            requestPermissionsLauncher.launch(missing)
        }

        viewModel.checkPermissionStatus()
        if (viewModel.permissionsGranted.value.orDefault()) {
            findNavController().navigate(R.id.homeFragment)
        }
    }
} 

private fun Boolean?.orDefault(): Boolean = this ?: false