package com.kreasaar.shrinkspace.ui.components

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.kreasaar.shrinkspace.R

class LoadingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MaterialCardView(context, attrs, defStyleAttr) {

    private val loadingAnimation: View
    private val circularProgressIndicator: CircularProgressIndicator
    private val loadingText: TextView
    private val progressText: TextView
    private val loadingContainer: LinearLayout

    private var rotationAnimator: ObjectAnimator? = null
    private var pulseAnimator: ValueAnimator? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.view_loading, this, true)
        loadingAnimation = findViewById(R.id.loading_animation)
        circularProgressIndicator = findViewById(R.id.circular_progress_indicator)
        loadingText = findViewById(R.id.loading_text)
        progressText = findViewById(R.id.progress_text)
        loadingContainer = findViewById(R.id.loading_container)

        setupLoadingCard()
        setupProgressIndicator()
    }

    private fun setupLoadingCard() {
        // Material Design 3 card styling
        cardElevation = resources.getDimension(R.dimen.loading_card_elevation)
        radius = resources.getDimension(R.dimen.loading_card_radius)
        strokeWidth = 0
        strokeColor = ContextCompat.getColor(context, R.color.card_stroke)
        
        // Enable ripple effect
        isClickable = true
        isFocusable = true
        ViewCompat.setBackgroundTintList(this, ContextCompat.getColorStateList(context, R.color.card_background))
    }

    private fun setupProgressIndicator() {
        // Material Design 3 circular progress indicator styling
        circularProgressIndicator.setIndicatorColor(ContextCompat.getColor(context, R.color.primary))
        circularProgressIndicator.trackColor = ContextCompat.getColor(context, R.color.progress_track)
        circularProgressIndicator.trackThickness = resources.getDimensionPixelSize(R.dimen.loading_progress_thickness)
        circularProgressIndicator.indicatorSize = resources.getDimensionPixelSize(R.dimen.loading_progress_size)
        circularProgressIndicator.max = 100
    }

    fun setLoadingText(text: String) {
        loadingText.text = text
    }

    fun setProgress(progress: Int, max: Int = 100) {
        val percentage = ((progress.toFloat() / max.toFloat()) * 100).toInt()
        progressText.text = "$percentage%"
        circularProgressIndicator.progress = percentage
    }

    fun setIndeterminate(indeterminate: Boolean) {
        circularProgressIndicator.isIndeterminate = indeterminate
        if (indeterminate) {
            progressText.text = ""
            loadingText.text = "Processing..."
        }
    }

    fun show() {
        visibility = View.VISIBLE
        alpha = 0f
        scaleX = 0.8f
        scaleY = 0.8f
        
        animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(400)
            .withEndAction {
                startLoadingAnimation()
            }
            .start()
    }

    fun hide() {
        animate()
            .alpha(0f)
            .scaleX(0.8f)
            .scaleY(0.8f)
            .setDuration(300)
            .withEndAction {
                visibility = View.GONE
                stopLoadingAnimation()
            }
            .start()
    }

    private fun startLoadingAnimation() {
        // Start rotation animation for loading icon
        rotationAnimator = ObjectAnimator.ofFloat(loadingAnimation, "rotation", 0f, 360f).apply {
            duration = 2000
            interpolator = LinearInterpolator()
            repeatCount = ObjectAnimator.INFINITE
            start()
        }

        // Start pulse animation for text
        pulseAnimator = ValueAnimator.ofFloat(1f, 1.1f, 1f).apply {
            duration = 1500
            interpolator = AccelerateDecelerateInterpolator()
            repeatCount = ValueAnimator.INFINITE
            
            addUpdateListener { animation ->
                val scale = animation.animatedValue as Float
                loadingText.scaleX = scale
                loadingText.scaleY = scale
            }
            
            start()
        }
    }

    private fun stopLoadingAnimation() {
        rotationAnimator?.cancel()
        pulseAnimator?.cancel()
    }

    fun setLoadingStyle(style: LoadingStyle) {
        when (style) {
            LoadingStyle.CIRCULAR -> {
                circularProgressIndicator.visibility = View.VISIBLE
                loadingAnimation.visibility = View.GONE
            }
            LoadingStyle.ANIMATED -> {
                circularProgressIndicator.visibility = View.GONE
                loadingAnimation.visibility = View.VISIBLE
            }
            LoadingStyle.MIXED -> {
                circularProgressIndicator.visibility = View.VISIBLE
                loadingAnimation.visibility = View.VISIBLE
            }
        }
    }

    fun setCompactMode(compact: Boolean) {
        if (compact) {
            loadingContainer.orientation = LinearLayout.HORIZONTAL
            circularProgressIndicator.indicatorSize = resources.getDimensionPixelSize(R.dimen.loading_progress_compact_size)
        } else {
            loadingContainer.orientation = LinearLayout.VERTICAL
            circularProgressIndicator.indicatorSize = resources.getDimensionPixelSize(R.dimen.loading_progress_size)
        }
    }

    enum class LoadingStyle {
        CIRCULAR, ANIMATED, MIXED
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopLoadingAnimation()
    }
} 