package com.kreasaar.shrinkspace.ui.components

import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import com.kreasaar.shrinkspace.R

class ErrorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MaterialCardView(context, attrs, defStyleAttr) {

    private val errorIcon: View
    private val errorTitle: TextView
    private val errorMessage: TextView
    private val retryButton: MaterialButton
    private val settingsButton: MaterialButton
    private val errorContainer: LinearLayout
    private val actionContainer: LinearLayout
    private val errorChip: Chip

    private var onRetryClickListener: (() -> Unit)? = null
    private var onSettingsClickListener: (() -> Unit)? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.view_error, this, true)
        errorIcon = findViewById(R.id.error_icon)
        errorTitle = findViewById(R.id.error_title)
        errorMessage = findViewById(R.id.error_message)
        retryButton = findViewById(R.id.retry_button)
        settingsButton = findViewById(R.id.settings_button)
        errorContainer = findViewById(R.id.error_container)
        actionContainer = findViewById(R.id.action_container)
        errorChip = findViewById(R.id.error_chip)

        setupErrorCard()
        setupAnimations()
    }

    private fun setupErrorCard() {
        // Material Design 3 card styling
        cardElevation = resources.getDimension(R.dimen.error_card_elevation)
        radius = resources.getDimension(R.dimen.error_card_radius)
        strokeWidth = 0
        strokeColor = ContextCompat.getColor(context, R.color.card_stroke)
        
        // Enable ripple effect
        isClickable = true
        isFocusable = true
        ViewCompat.setBackgroundTintList(this, ContextCompat.getColorStateList(context, R.color.card_background))
    }

    private fun setupAnimations() {
        // Animate error icon on show
        errorIcon.alpha = 0f
        errorIcon.scaleX = 0.8f
        errorIcon.scaleY = 0.8f
    }

    fun setError(
        title: String,
        message: String,
        errorType: ErrorType = ErrorType.GENERAL,
        showRetry: Boolean = true,
        showSettings: Boolean = false,
        onRetryClick: (() -> Unit)? = null,
        onSettingsClick: (() -> Unit)? = null
    ) {
        errorTitle.text = title
        errorMessage.text = message
        
        this.onRetryClickListener = onRetryClick
        this.onSettingsClickListener = onSettingsClick

        setupErrorType(errorType)
        setupActionButtons(showRetry, showSettings)
    }

    private fun setupErrorType(type: ErrorType) {
        when (type) {
            ErrorType.PERMISSION_DENIED -> {
                errorIcon.setBackgroundResource(R.drawable.ic_permission_error)
                errorChip.text = "Permission"
                errorChip.chipBackgroundColor = ContextCompat.getColorStateList(context, R.color.error_permission_chip)
            }
            ErrorType.STORAGE_FULL -> {
                errorIcon.setBackgroundResource(R.drawable.ic_storage_error)
                errorChip.text = "Storage"
                errorChip.chipBackgroundColor = ContextCompat.getColorStateList(context, R.color.error_storage_chip)
            }
            ErrorType.NETWORK_ERROR -> {
                errorIcon.setBackgroundResource(R.drawable.ic_network_error)
                errorChip.text = "Network"
                errorChip.chipBackgroundColor = ContextCompat.getColorStateList(context, R.color.error_network_chip)
            }
            ErrorType.MEDIA_ACCESS_ERROR -> {
                errorIcon.setBackgroundResource(R.drawable.ic_media_error)
                errorChip.text = "Media"
                errorChip.chipBackgroundColor = ContextCompat.getColorStateList(context, R.color.error_media_chip)
            }
            ErrorType.COMPRESSION_ERROR -> {
                errorIcon.setBackgroundResource(R.drawable.ic_compression_error)
                errorChip.text = "Compression"
                errorChip.chipBackgroundColor = ContextCompat.getColorStateList(context, R.color.error_compression_chip)
            }
            ErrorType.GENERAL -> {
                errorIcon.setBackgroundResource(R.drawable.ic_general_error)
                errorChip.text = "Error"
                errorChip.chipBackgroundColor = ContextCompat.getColorStateList(context, R.color.error_general_chip)
            }
        }
    }

    private fun setupActionButtons(showRetry: Boolean, showSettings: Boolean) {
        if (showRetry && onRetryClickListener != null) {
            retryButton.visibility = View.VISIBLE
            retryButton.setOnClickListener { onRetryClickListener?.invoke() }
        } else {
            retryButton.visibility = View.GONE
        }

        if (showSettings && onSettingsClickListener != null) {
            settingsButton.visibility = View.VISIBLE
            settingsButton.setOnClickListener { onSettingsClickListener?.invoke() }
        } else {
            settingsButton.visibility = View.GONE
        }
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
                animateErrorIcon()
            }
            .start()
    }

    private fun animateErrorIcon() {
        val scaleAnimator = ObjectAnimator.ofFloat(errorIcon, "scaleX", 0.8f, 1f)
        val alphaAnimator = ObjectAnimator.ofFloat(errorIcon, "alpha", 0f, 1f)
        
        scaleAnimator.duration = 600
        scaleAnimator.interpolator = AccelerateDecelerateInterpolator()
        
        alphaAnimator.duration = 400
        alphaAnimator.interpolator = AccelerateDecelerateInterpolator()
        
        scaleAnimator.start()
        alphaAnimator.start()
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
            errorContainer.orientation = LinearLayout.HORIZONTAL
            actionContainer.orientation = LinearLayout.HORIZONTAL
        } else {
            errorContainer.orientation = LinearLayout.VERTICAL
            actionContainer.orientation = LinearLayout.VERTICAL
        }
    }

    enum class ErrorType {
        PERMISSION_DENIED, STORAGE_FULL, NETWORK_ERROR, MEDIA_ACCESS_ERROR, COMPRESSION_ERROR, GENERAL
    }
} 