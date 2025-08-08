package com.kreasaar.shrinkspace.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kreasaar.shrinkspace.R
import com.kreasaar.shrinkspace.viewmodel.GalleryViewModel
import com.kreasaar.shrinkspace.adapters.MediaAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GalleryFragment : Fragment() {
    private val viewModel: GalleryViewModel by viewModels()
    private var recyclerView: RecyclerView? = null
    private lateinit var adapter: MediaAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate gallery layout and show media list
        return inflater.inflate(R.layout.fragment_gallery, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.galleryRecycler)
        adapter = MediaAdapter(
            onItemClick = { /* open details or preview */ },
            onItemLongClick = { item ->
                viewModel.toggleItemSelection(item.id)
                true
            }
        )
        recyclerView?.layoutManager = GridLayoutManager(requireContext(), 3)
        recyclerView?.adapter = adapter

        view.findViewById<android.view.View>(R.id.selectAllButton)?.setOnClickListener {
            viewModel.selectAll()
        }
        view.findViewById<android.view.View>(R.id.compressButton)?.setOnClickListener {
            // Navigate to compression options with selected items later (for now, just open)
            findNavController().navigate(R.id.compressionOptionsFragment)
        }

        viewModel.galleryItems.observe(viewLifecycleOwner) { items ->
            adapter.submitList(items)
        }
    }
} 