package com.kreasaar.shrinkspace.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import com.kreasaar.shrinkspace.R
import com.kreasaar.shrinkspace.data.MediaItem
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

class MediaItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MaterialCardView(context, attrs, defStyleAttr) {

    private val imageView: ImageView
    private val titleText: TextView
    private val sizeText: TextView
    private val typeChip: Chip
    private val checkBox: ImageView
    private val overlayView: View

    private var isSelected = false
    private var onItemClickListener: ((MediaItem) -> Unit)? = null
    private var onLongClickListener: ((MediaItem) -> Unit)? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.view_media_item, this, true)
        imageView = findViewById(R.id.media_image)
        titleText = findViewById(R.id.media_title)
        sizeText = findViewById(R.id.media_size)
        typeChip = findViewById(R.id.type_chip)
        checkBox = findViewById(R.id.media_checkbox)
        overlayView = findViewById(R.id.overlay_view)

        setupCard()
        setupClickListeners()
    }

    private fun setupCard() {
        // Material Design 3 card styling
        cardElevation = resources.getDimension(R.dimen.card_elevation)
        radius = resources.getDimension(R.dimen.card_corner_radius)
        strokeWidth = 0
        strokeColor = ContextCompat.getColor(context, R.color.card_stroke)
        
        // Enable ripple effect
        isClickable = true
        isFocusable = true
        ViewCompat.setBackgroundTintList(this, ContextCompat.getColorStateList(context, R.color.card_background))
    }

    private fun setupClickListeners() {
        setOnClickListener {
            onItemClickListener?.invoke(currentMediaItem)
        }

        setOnLongClickListener {
            onLongClickListener?.invoke(currentMediaItem)
            true
        }
    }

    private var currentMediaItem: MediaItem = MediaItem(
        id = -1,
        name = "",
        uri = android.net.Uri.EMPTY,
        type = "",
        size = 0L,
        dateAdded = 0L,
        path = ""
    )

    fun bind(mediaItem: MediaItem, isSelected: Boolean = false) {
        currentMediaItem = mediaItem
        
        titleText.text = mediaItem.path.substringAfterLast('/')
        sizeText.text = formatFileSize(mediaItem.size)
        
        // Set media type chip
        typeChip.text = getMediaTypeLabel(mediaItem.type)
        typeChip.chipBackgroundColor = ContextCompat.getColorStateList(context, getMediaTypeColor(mediaItem.type))
        
        setSelectionState(isSelected)
        
        loadImage(mediaItem)
    }

    private fun loadImage(mediaItem: MediaItem) {
        // Load image using Glide or Coil with placeholder and error handling
        Glide.with(context)
            .load(mediaItem.uri)
            .placeholder(R.drawable.ic_media_placeholder)
            .error(R.drawable.ic_media_error)
            .centerCrop()
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(imageView)
    }

    private fun setSelectionState(selected: Boolean) {
        isSelected = selected
        
        if (selected) {
            checkBox.visibility = View.VISIBLE
            overlayView.visibility = View.VISIBLE
            strokeWidth = resources.getDimensionPixelSize(R.dimen.card_selected_stroke)
            strokeColor = ContextCompat.getColor(context, R.color.primary)
        } else {
            checkBox.visibility = View.GONE
            overlayView.visibility = View.GONE
            strokeWidth = 0
        }
    }

    private fun formatFileSize(bytes: Long): String {
        return when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> "${bytes / 1024} KB"
            bytes < 1024 * 1024 * 1024 -> "${bytes / (1024 * 1024)} MB"
            else -> "${bytes / (1024 * 1024 * 1024)} GB"
        }
    }

    private fun getMediaTypeLabel(type: String): String {
        return when (type.lowercase()) {
            "image" -> "Photo"
            "video" -> "Video"
            "audio" -> "Audio"
            else -> "File"
        }
    }

    private fun getMediaTypeColor(type: String): Int {
        return when (type.lowercase()) {
            "image" -> R.color.chip_image_background
            "video" -> R.color.chip_video_background
            "audio" -> R.color.chip_audio_background
            else -> R.color.chip_file_background
        }
    }

    fun setOnItemClickListener(listener: (MediaItem) -> Unit) {
        onItemClickListener = listener
    }

    fun setOnLongClickListener(listener: (MediaItem) -> Unit) {
        onLongClickListener = listener
    }

    fun toggleSelection() {
        setSelectionState(!isSelected)
    }
} 