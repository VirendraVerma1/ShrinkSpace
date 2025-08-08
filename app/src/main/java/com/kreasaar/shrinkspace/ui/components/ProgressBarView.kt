package com.kreasaar.shrinkspace.ui.components

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.kreasaar.shrinkspace.R

class ProgressBarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val progressIndicator: LinearProgressIndicator
    private val percentageText: TextView
    private val statusText: TextView
    private val progressContainer: LinearLayout

    private var currentProgress = 0
    private var animator: ValueAnimator? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.view_progress_bar, this, true)
        progressIndicator = findViewById(R.id.progress_indicator)
        percentageText = findViewById(R.id.percentage_text)
        statusText = findViewById(R.id.status_text)
        progressContainer = findViewById(R.id.progress_container)

        setupProgressIndicator()
    }

    private fun setupProgressIndicator() {
        // Material Design 3 progress indicator styling
        progressIndicator.setIndicatorColor(ContextCompat.getColor(context, R.color.primary))
        progressIndicator.trackColor = ContextCompat.getColor(context, R.color.progress_track)
        progressIndicator.trackThickness = resources.getDimensionPixelSize(R.dimen.progress_track_thickness)
        
        // Set indeterminate animation
        progressIndicator.isIndeterminate = false
        progressIndicator.max = 100
    }

    fun setProgress(progress: Int, status: String = "", animate: Boolean = true) {
        if (animate && progress != currentProgress) {
            animateProgress(currentProgress, progress)
        } else {
            progressIndicator.progress = progress
            percentageText.text = "$progress%"
        }
        
        if (status.isNotEmpty()) {
            statusText.text = status
            statusText.visibility = VISIBLE
        } else {
            statusText.visibility = GONE
        }
        
        currentProgress = progress
    }

    private fun animateProgress(from: Int, to: Int) {
        animator?.cancel()
        
        animator = ValueAnimator.ofInt(from, to).apply {
            duration = 500
            interpolator = AccelerateDecelerateInterpolator()
            
            addUpdateListener { animation ->
                val animatedValue = animation.animatedValue as Int
                progressIndicator.progress = animatedValue
                percentageText.text = "$animatedValue%"
            }
            
            start()
        }
    }

    fun setMax(max: Int) {
        progressIndicator.max = max
    }

    fun setIndeterminate(indeterminate: Boolean) {
        progressIndicator.isIndeterminate = indeterminate
        if (indeterminate) {
            percentageText.text = ""
            statusText.text = "Processing..."
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

    fun setProgressStyle(style: ProgressStyle) {
        when (style) {
            ProgressStyle.LINEAR -> {
                progressIndicator.visibility = VISIBLE
                progressContainer.visibility = GONE
            }
            ProgressStyle.CIRCULAR -> {
                progressIndicator.visibility = GONE
                progressContainer.visibility = VISIBLE
            }
        }
    }

    enum class ProgressStyle {
        LINEAR, CIRCULAR
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animator?.cancel()
    }
} 