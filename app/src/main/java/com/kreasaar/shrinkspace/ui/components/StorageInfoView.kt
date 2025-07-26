package com.kreasaar.shrinkspace.ui.components

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.kreasaar.shrinkspace.R

class StorageInfoView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MaterialCardView(context, attrs, defStyleAttr) {

    private val storageProgressIndicator: LinearProgressIndicator
    private val usedStorageText: TextView
    private val totalStorageText: TextView
    private val availableStorageText: TextView
    private val mediaCountText: TextView
    private val storageCard: MaterialCardView
    private val storageDetailsContainer: LinearLayout

    private var animator: ValueAnimator? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.view_storage_info, this, true)
        storageProgressIndicator = findViewById(R.id.storage_progress_indicator)
        usedStorageText = findViewById(R.id.used_storage_text)
        totalStorageText = findViewById(R.id.total_storage_text)
        availableStorageText = findViewById(R.id.available_storage_text)
        mediaCountText = findViewById(R.id.media_count_text)
        storageCard = findViewById(R.id.storage_card)
        storageDetailsContainer = findViewById(R.id.storage_details_container)

        setupStorageCard()
        setupProgressIndicator()
    }

    private fun setupStorageCard() {
        // Material Design 3 card styling
        cardElevation = resources.getDimension(R.dimen.storage_card_elevation)
        radius = resources.getDimension(R.dimen.storage_card_radius)
        strokeWidth = 0
        strokeColor = ContextCompat.getColor(context, R.color.card_stroke)
        
        // Enable ripple effect
        isClickable = true
        isFocusable = true
        ViewCompat.setBackgroundTintList(this, ContextCompat.getColorStateList(context, R.color.card_background))
    }

    private fun setupProgressIndicator() {
        // Material Design 3 progress indicator styling
        storageProgressIndicator.setIndicatorColor(ContextCompat.getColor(context, R.color.primary))
        storageProgressIndicator.trackColor = ContextCompat.getColor(context, R.color.progress_track)
        storageProgressIndicator.trackThickness = resources.getDimensionPixelSize(R.dimen.storage_progress_thickness)
        storageProgressIndicator.indicatorSize = resources.getDimensionPixelSize(R.dimen.storage_progress_size)
    }

    fun updateStorageInfo(
        usedStorage: Long,
        totalStorage: Long,
        mediaCount: Int,
        animate: Boolean = true
    ) {
        val availableStorage = totalStorage - usedStorage
        val percentage = ((usedStorage.toFloat() / totalStorage.toFloat()) * 100).toInt()

        if (animate) {
            animateStorageProgress(percentage)
        } else {
            storageProgressIndicator.progress = percentage
        }

        usedStorageText.text = formatFileSize(usedStorage)
        totalStorageText.text = formatFileSize(totalStorage)
        availableStorageText.text = formatFileSize(availableStorage)
        mediaCountText.text = "$mediaCount files"

        // Update storage status color based on usage
        updateStorageStatusColor(percentage)
    }

    private fun animateStorageProgress(percentage: Int) {
        animator?.cancel()
        
        animator = ValueAnimator.ofInt(0, percentage).apply {
            duration = 1000
            interpolator = AccelerateDecelerateInterpolator()
            
            addUpdateListener { animation ->
                val animatedValue = animation.animatedValue as Int
                storageProgressIndicator.progress = animatedValue
            }
            
            start()
        }
    }

    private fun updateStorageStatusColor(percentage: Int) {
        val colorRes = when {
            percentage >= 90 -> R.color.storage_critical
            percentage >= 75 -> R.color.storage_warning
            percentage >= 50 -> R.color.storage_normal
            else -> R.color.storage_good
        }
        
        storageProgressIndicator.setIndicatorColor(ContextCompat.getColor(context, colorRes))
    }

    private fun formatFileSize(bytes: Long): String {
        return when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> "${bytes / 1024} KB"
            bytes < 1024 * 1024 * 1024 -> "${bytes / (1024 * 1024)} MB"
            else -> "${bytes / (1024 * 1024 * 1024)} GB"
        }
    }

    fun setCompactMode(compact: Boolean) {
        if (compact) {
            storageDetailsContainer.visibility = GONE
            storageProgressIndicator.trackThickness = resources.getDimensionPixelSize(R.dimen.storage_progress_compact_thickness)
        } else {
            storageDetailsContainer.visibility = VISIBLE
            storageProgressIndicator.trackThickness = resources.getDimensionPixelSize(R.dimen.storage_progress_thickness)
        }
    }

    fun show() {
        visibility = VISIBLE
        alpha = 0f
        animate()
            .alpha(1f)
            .setDuration(300)
            .start()
    }

    fun hide() {
        animate()
            .alpha(0f)
            .setDuration(300)
            .withEndAction {
                visibility = GONE
            }
            .start()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animator?.cancel()
    }
} 