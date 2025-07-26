package com.kreasaar.shrinkspace.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.kreasaar.shrinkspace.R
import com.kreasaar.shrinkspace.data.MediaItem

class MediaAdapter(
    private val onItemClick: (MediaItem) -> Unit,
    private val onItemLongClick: (MediaItem) -> Boolean
) : ListAdapter<MediaItem, MediaAdapter.MediaViewHolder>(MediaDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_media, parent, false)
        return MediaViewHolder(view)
    }

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MediaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.media_image)
        private val nameText: TextView = itemView.findViewById(R.id.media_name)
        private val sizeText: TextView = itemView.findViewById(R.id.media_size)
        private val typeText: TextView = itemView.findViewById(R.id.media_type)

        fun bind(mediaItem: MediaItem) {
            nameText.text = mediaItem.name
            sizeText.text = formatFileSize(mediaItem.size)
            typeText.text = mediaItem.type.uppercase()

            // Load image using Glide or Coil
            Glide.with(itemView.context)
                .load(mediaItem.uri)
                .placeholder(R.drawable.ic_media_placeholder)
                .error(R.drawable.ic_media_error)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView)

            itemView.setOnClickListener {
                onItemClick(mediaItem)
            }

            itemView.setOnLongClickListener {
                onItemLongClick(mediaItem)
            }
        }

        private fun formatFileSize(size: Long): String {
            return when {
                size >= 1024 * 1024 * 1024 -> "${size / (1024 * 1024 * 1024)} GB"
                size >= 1024 * 1024 -> "${size / (1024 * 1024)} MB"
                size >= 1024 -> "${size / 1024} KB"
                else -> "$size B"
            }
        }
    }

    private class MediaDiffCallback : DiffUtil.ItemCallback<MediaItem>() {
        override fun areItemsTheSame(oldItem: MediaItem, newItem: MediaItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MediaItem, newItem: MediaItem): Boolean {
            return oldItem == newItem
        }
    }
} 