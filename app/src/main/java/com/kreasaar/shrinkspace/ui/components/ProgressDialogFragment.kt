package com.kreasaar.shrinkspace.ui.components

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kreasaar.shrinkspace.R

class ProgressDialogFragment : DialogFragment() {
    
    private var title: String = "Processing..."
    private var message: String = "Please wait while we process your files."
    private var progress: Int = 0
    private var isIndeterminate: Boolean = false
    private var onCancel: (() -> Unit)? = null
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Replace with custom layout containing ProgressBar and TextView
        return inflater.inflate(R.layout.fragment_progress_dialog, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val titleText = view.findViewById<TextView>(R.id.title_text)
        val messageText = view.findViewById<TextView>(R.id.message_text)
        val progressBar = view.findViewById<ProgressBar>(R.id.progress_bar)
        val progressText = view.findViewById<TextView>(R.id.progress_text)
        
        titleText.text = title
        messageText.text = message
        
        if (isIndeterminate) {
            progressBar.isIndeterminate = true
            progressText.visibility = View.GONE
        } else {
            progressBar.isIndeterminate = false
            progressBar.progress = progress
            progressText.text = "$progress%"
            progressText.visibility = View.VISIBLE
        }
    }
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setView(R.layout.fragment_progress_dialog)
            .setCancelable(false)
            .setNegativeButton("Cancel") { _, _ ->
                onCancel?.invoke()
            }
            .create()
    }
    
    fun setProgress(newProgress: Int) {
        progress = newProgress
        view?.findViewById<ProgressBar>(R.id.progress_bar)?.progress = progress
        view?.findViewById<TextView>(R.id.progress_text)?.text = "$progress%"
    }
    
    fun setIndeterminate(indeterminate: Boolean) {
        isIndeterminate = indeterminate
        view?.findViewById<ProgressBar>(R.id.progress_bar)?.isIndeterminate = indeterminate
        view?.findViewById<TextView>(R.id.progress_text)?.visibility = 
            if (indeterminate) View.GONE else View.VISIBLE
    }
    
    fun setTitle(newTitle: String) {
        title = newTitle
        view?.findViewById<TextView>(R.id.title_text)?.text = title
    }
    
    fun setMessage(newMessage: String) {
        message = newMessage
        view?.findViewById<TextView>(R.id.message_text)?.text = message
    }
    
    fun setOnCancelListener(listener: () -> Unit) {
        onCancel = listener
    }
    
    companion object {
        const val TAG = "ProgressDialogFragment"
        
        fun newInstance(
            title: String = "Processing...",
            message: String = "Please wait while we process your files.",
            progress: Int = 0,
            isIndeterminate: Boolean = false
        ): ProgressDialogFragment {
            return ProgressDialogFragment().apply {
                this.title = title
                this.message = message
                this.progress = progress
                this.isIndeterminate = isIndeterminate
            }
        }
    }
} 