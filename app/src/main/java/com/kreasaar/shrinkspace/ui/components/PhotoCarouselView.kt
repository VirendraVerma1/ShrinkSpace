package com.kreasaar.shrinkspace.ui.components

import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.card.MaterialCardView
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.kreasaar.shrinkspace.R
import com.kreasaar.shrinkspace.data.MediaItem
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

class PhotoCarouselView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MaterialCardView(context, attrs, defStyleAttr) {

    private val viewPager: ViewPager2
    private val pageIndicator: TextView
    private val selectButton: MaterialButton
    private val skipButton: MaterialButton
    private val carouselContainer: FrameLayout
    private val actionContainer: FrameLayout
    private val photoInfoChip: Chip

    private var onPhotoSelectedListener: ((MediaItem) -> Unit)? = null
    private var onPhotoSkippedListener: ((MediaItem) -> Unit)? = null
    private var currentPhotos: List<MediaItem> = emptyList()

    init {
        LayoutInflater.from(context).inflate(R.layout.view_photo_carousel, this, true)
        viewPager = findViewById(R.id.photo_view_pager)
        pageIndicator = findViewById(R.id.page_indicator)
        selectButton = findViewById(R.id.select_button)
        skipButton = findViewById(R.id.skip_button)
        carouselContainer = findViewById(R.id.carousel_container)
        actionContainer = findViewById(R.id.action_container)
        photoInfoChip = findViewById(R.id.photo_info_chip)

        setupCarouselCard()
        setupViewPager()
        setupButtons()
    }

    private fun setupCarouselCard() {
        // Material Design 3 card styling
        cardElevation = resources.getDimension(R.dimen.carousel_card_elevation)
        radius = resources.getDimension(R.dimen.carousel_card_radius)
        strokeWidth = 0
        strokeColor = ContextCompat.getColor(context, R.color.card_stroke)
        
        // Enable ripple effect
        isClickable = true
        isFocusable = true
        ViewCompat.setBackgroundTintList(this, ContextCompat.getColorStateList(context, R.color.card_background))
    }

    private fun setupViewPager() {
        val adapter = PhotoCarouselAdapter()
        viewPager.adapter = adapter
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updatePageIndicator(position)
                updatePhotoInfo(position)
            }
        })
    }

    private fun setupButtons() {
        selectButton.setOnClickListener {
            val currentItem = getCurrentPhoto()
            currentItem?.let { onPhotoSelectedListener?.invoke(it) }
        }

        skipButton.setOnClickListener {
            val currentItem = getCurrentPhoto()
            currentItem?.let { onPhotoSkippedListener?.invoke(it) }
        }
    }

    fun setPhotos(photos: List<MediaItem>) {
        currentPhotos = photos
        updatePageIndicator(0)
        updatePhotoInfo(0)
    }

    private fun updatePageIndicator(position: Int) {
        val totalPages = currentPhotos.size
        if (totalPages > 0) {
            pageIndicator.text = "${position + 1} / $totalPages"
        }
    }

    private fun updatePhotoInfo(position: Int) {
        if (position < currentPhotos.size) {
            val photo = currentPhotos[position]
            val mediaType = getMediaTypeLabel(photo.type)
            val fileSize = formatFileSize(photo.size)
            photoInfoChip.text = "$mediaType • $fileSize"
        }
    }

    private fun getMediaTypeLabel(type: String): String {
        return when (type.lowercase()) {
            "image" -> "Image"
            "video" -> "Video"
            else -> "Media"
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

    // Set up adapter for ViewPager2
    private inner class PhotoCarouselAdapter : RecyclerView.Adapter<PhotoCarouselAdapter.PhotoViewHolder>() {
        
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
            val imageView = ImageView(parent.context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
            return PhotoViewHolder(imageView)
        }

        override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
            val photo = currentPhotos[position]
            Glide.with(holder.imageView.context)
                .load(photo.uri)
                .placeholder(R.drawable.ic_media_placeholder)
                .error(R.drawable.ic_media_error)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.imageView)
        }

        override fun getItemCount(): Int = currentPhotos.size

        inner class PhotoViewHolder(val imageView: ImageView) : RecyclerView.ViewHolder(imageView)
    }

    private fun getCurrentPhoto(): MediaItem? {
        val currentPosition = viewPager.currentItem
        return if (currentPosition < currentPhotos.size) {
            currentPhotos[currentPosition]
        } else null
    }

    fun setOnPhotoSelectedListener(listener: (MediaItem) -> Unit) {
        onPhotoSelectedListener = listener
    }

    fun setOnPhotoSkippedListener(listener: (MediaItem) -> Unit) {
        onPhotoSkippedListener = listener
    }

    fun show() {
        visibility = View.VISIBLE
        alpha = 0f
        scaleX = 0.9f
        scaleY = 0.9f
        
        animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(400)
            .withEndAction {
                animateCarouselEntry()
            }
            .start()
    }

    private fun animateCarouselEntry() {
        val slideAnimator = ObjectAnimator.ofFloat(carouselContainer, "translationY", 100f, 0f)
        val fadeAnimator = ObjectAnimator.ofFloat(actionContainer, "alpha", 0f, 1f)
        
        slideAnimator.duration = 600
        slideAnimator.interpolator = AccelerateDecelerateInterpolator()
        
        fadeAnimator.duration = 800
        fadeAnimator.interpolator = AccelerateDecelerateInterpolator()
        
        slideAnimator.start()
        fadeAnimator.start()
    }

    fun hide() {
        animate()
            .alpha(0f)
            .scaleX(0.9f)
            .scaleY(0.9f)
            .setDuration(300)
            .withEndAction {
                visibility = View.GONE
            }
            .start()
    }

    fun setCompactMode(compact: Boolean) {
        if (compact) {
            // Adjust layout for compact mode
            carouselContainer.layoutParams.height = resources.getDimensionPixelSize(R.dimen.carousel_compact_height)
            actionContainer.layoutParams.height = resources.getDimensionPixelSize(R.dimen.action_compact_height)
        } else {
            // Normal mode
            carouselContainer.layoutParams.height = resources.getDimensionPixelSize(R.dimen.carousel_height)
            actionContainer.layoutParams.height = resources.getDimensionPixelSize(R.dimen.action_height)
        }
    }

    fun setCarouselStyle(style: CarouselStyle) {
        when (style) {
            CarouselStyle.FULLSCREEN -> {
                cardElevation = 0f
                radius = 0f
            }
            CarouselStyle.CARD -> {
                cardElevation = resources.getDimension(R.dimen.carousel_card_elevation)
                radius = resources.getDimension(R.dimen.carousel_card_radius)
            }
            CarouselStyle.MINIMAL -> {
                cardElevation = resources.getDimension(R.dimen.carousel_minimal_elevation)
                radius = resources.getDimension(R.dimen.carousel_minimal_radius)
            }
        }
    }

    enum class CarouselStyle {
        FULLSCREEN, CARD, MINIMAL
    }
} 