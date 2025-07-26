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
import com.google.android.material.slider.Slider
import com.google.android.material.chip.Chip
import com.kreasaar.shrinkspace.R

class CompressionQualitySlider @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MaterialCardView(context, attrs, defStyleAttr) {

    private val qualitySlider: Slider
    private val qualityText: TextView
    private val sizeReductionText: TextView
    private val previewText: TextView
    private val qualityChip: Chip
    private val compressionContainer: LinearLayout

    private var onQualityChangedListener: ((Int) -> Unit)? = null
    private var animator: ValueAnimator? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.view_compression_quality, this, true)
        qualitySlider = findViewById(R.id.quality_slider)
        qualityText = findViewById(R.id.quality_text)
        sizeReductionText = findViewById(R.id.size_reduction_text)
        previewText = findViewById(R.id.preview_text)
        qualityChip = findViewById(R.id.quality_chip)
        compressionContainer = findViewById(R.id.compression_container)

        setupCompressionCard()
        setupSlider()
    }

    private fun setupCompressionCard() {
        // Material Design 3 card styling
        cardElevation = resources.getDimension(R.dimen.compression_card_elevation)
        radius = resources.getDimension(R.dimen.compression_card_radius)
        strokeWidth = 0
        strokeColor = ContextCompat.getColor(context, R.color.card_stroke)
        
        // Enable ripple effect
        isClickable = true
        isFocusable = true
        ViewCompat.setBackgroundTintList(this, ContextCompat.getColorStateList(context, R.color.card_background))
    }

    private fun setupSlider() {
        // Material Design 3 slider styling
        qualitySlider.valueFrom = 0f
        qualitySlider.valueTo = 100f
        qualitySlider.value = 80f
        qualitySlider.stepSize = 5f

        // Set slider colors
        qualitySlider.trackActiveTintList = ContextCompat.getColorStateList(context, R.color.slider_active)
        qualitySlider.trackInactiveTintList = ContextCompat.getColorStateList(context, R.color.slider_inactive)
        qualitySlider.thumbColor = ContextCompat.getColorStateList(context, R.color.slider_thumb)

        qualitySlider.addOnChangeListener { _, value, fromUser ->
            if (fromUser) {
                updateQualityDisplay(value.toInt())
                onQualityChangedListener?.invoke(value.toInt())
            }
        }

        updateQualityDisplay(80)
    }

    private fun updateQualityDisplay(quality: Int) {
        qualityText.text = "Quality: $quality%"
        
        val reduction = when {
            quality >= 90 -> "Minimal reduction"
            quality >= 70 -> "Small reduction"
            quality >= 50 -> "Medium reduction"
            else -> "High reduction"
        }
        sizeReductionText.text = reduction

        val preview = when {
            quality >= 90 -> "Excellent quality"
            quality >= 70 -> "Good quality"
            quality >= 50 -> "Acceptable quality"
            else -> "Lower quality"
        }
        previewText.text = preview

        // Update chip
        qualityChip.text = when {
            quality >= 90 -> "High"
            quality >= 70 -> "Good"
            quality >= 50 -> "Medium"
            else -> "Low"
        }
        qualityChip.chipBackgroundColor = ContextCompat.getColorStateList(context, getQualityColor(quality))
    }

    private fun getQualityColor(quality: Int): Int {
        return when {
            quality >= 90 -> R.color.quality_high_chip
            quality >= 70 -> R.color.quality_good_chip
            quality >= 50 -> R.color.quality_medium_chip
            else -> R.color.quality_low_chip
        }
    }

    fun setQuality(quality: Int, animate: Boolean = true) {
        if (animate) {
            animateQualityChange(qualitySlider.value.toInt(), quality)
        } else {
            qualitySlider.value = quality.toFloat()
            updateQualityDisplay(quality)
        }
    }

    private fun animateQualityChange(from: Int, to: Int) {
        animator?.cancel()
        
        animator = ValueAnimator.ofInt(from, to).apply {
            duration = 500
            interpolator = AccelerateDecelerateInterpolator()
            
            addUpdateListener { animation ->
                val animatedValue = animation.animatedValue as Int
                qualitySlider.value = animatedValue.toFloat()
                updateQualityDisplay(animatedValue)
            }
            
            start()
        }
    }

    fun getQuality(): Int {
        return qualitySlider.value.toInt()
    }

    fun setOnQualityChangedListener(listener: (Int) -> Unit) {
        onQualityChangedListener = listener
    }

    fun setCompactMode(compact: Boolean) {
        if (compact) {
            compressionContainer.orientation = LinearLayout.HORIZONTAL
            qualitySlider.trackThickness = resources.getDimensionPixelSize(R.dimen.slider_compact_thickness)
        } else {
            compressionContainer.orientation = LinearLayout.VERTICAL
            qualitySlider.trackThickness = resources.getDimensionPixelSize(R.dimen.slider_thickness)
        }
    }

    fun show() {
        visibility = VISIBLE
        alpha = 0f
        scaleX = 0.9f
        scaleY = 0.9f
        
        animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(400)
            .start()
    }

    fun hide() {
        animate()
            .alpha(0f)
            .scaleX(0.9f)
            .scaleY(0.9f)
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