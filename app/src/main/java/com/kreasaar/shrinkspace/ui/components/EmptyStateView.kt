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

class EmptyStateView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MaterialCardView(context, attrs, defStyleAttr) {

    private val illustrationView: View
    private val titleText: TextView
    private val messageText: TextView
    private val actionButton: MaterialButton
    private val secondaryActionButton: MaterialButton
    private val emptyStateContainer: LinearLayout
    private val actionContainer: LinearLayout

    private var onPrimaryActionClick: (() -> Unit)? = null
    private var onSecondaryActionClick: (() -> Unit)? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.view_empty_state, this, true)
        illustrationView = findViewById(R.id.illustration_view)
        titleText = findViewById(R.id.title_text)
        messageText = findViewById(R.id.message_text)
        actionButton = findViewById(R.id.action_button)
        secondaryActionButton = findViewById(R.id.secondary_action_button)
        emptyStateContainer = findViewById(R.id.empty_state_container)
        actionContainer = findViewById(R.id.action_container)

        setupEmptyStateCard()
        setupAnimations()
    }

    private fun setupEmptyStateCard() {
        // Material Design 3 card styling
        cardElevation = resources.getDimension(R.dimen.empty_state_card_elevation)
        radius = resources.getDimension(R.dimen.empty_state_card_radius)
        strokeWidth = 0
        strokeColor = ContextCompat.getColor(context, R.color.card_stroke)
        
        // Enable ripple effect
        isClickable = true
        isFocusable = true
        ViewCompat.setBackgroundTintList(this, ContextCompat.getColorStateList(context, R.color.card_background))
    }

    private fun setupAnimations() {
        // Animate illustration on show
        illustrationView.alpha = 0f
        illustrationView.scaleX = 0.8f
        illustrationView.scaleY = 0.8f
    }

    fun setEmptyState(
        title: String,
        message: String,
        primaryActionText: String? = null,
        secondaryActionText: String? = null,
        onPrimaryActionClick: (() -> Unit)? = null,
        onSecondaryActionClick: (() -> Unit)? = null,
        illustrationType: IllustrationType = IllustrationType.EMPTY
    ) {
        titleText.text = title
        messageText.text = message
        
        this.onPrimaryActionClick = onPrimaryActionClick
        this.onSecondaryActionClick = onSecondaryActionClick

        setupActionButtons(primaryActionText, secondaryActionText)
        setupIllustration(illustrationType)
    }

    private fun setupActionButtons(primaryText: String?, secondaryText: String?) {
        if (primaryText != null && onPrimaryActionClick != null) {
            actionButton.text = primaryText
            actionButton.setOnClickListener { onPrimaryActionClick?.invoke() }
            actionButton.visibility = View.VISIBLE
        } else {
            actionButton.visibility = View.GONE
        }

        if (secondaryText != null && onSecondaryActionClick != null) {
            secondaryActionButton.text = secondaryText
            secondaryActionButton.setOnClickListener { onSecondaryActionClick?.invoke() }
            secondaryActionButton.visibility = View.VISIBLE
        } else {
            secondaryActionButton.visibility = View.GONE
        }
    }

    private fun setupIllustration(type: IllustrationType) {
        // Set illustration based on type
        when (type) {
            IllustrationType.EMPTY -> {
                illustrationView.setBackgroundResource(R.drawable.ic_empty_state)
            }
            IllustrationType.NO_PERMISSIONS -> {
                illustrationView.setBackgroundResource(R.drawable.ic_no_permissions)
            }
            IllustrationType.NO_MEDIA -> {
                illustrationView.setBackgroundResource(R.drawable.ic_no_media)
            }
            IllustrationType.ERROR -> {
                illustrationView.setBackgroundResource(R.drawable.ic_error_state)
            }
        }
    }

    fun show() {
        visibility = View.VISIBLE
        alpha = 0f
        
        animate()
            .alpha(1f)
            .setDuration(400)
            .withEndAction {
                animateIllustration()
            }
            .start()
    }

    private fun animateIllustration() {
        val scaleAnimator = ObjectAnimator.ofFloat(illustrationView, "scaleX", 0.8f, 1f)
        val alphaAnimator = ObjectAnimator.ofFloat(illustrationView, "alpha", 0f, 1f)
        
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
            .setDuration(300)
            .withEndAction {
                visibility = View.GONE
            }
            .start()
    }

    fun setCompactMode(compact: Boolean) {
        if (compact) {
            emptyStateContainer.orientation = LinearLayout.HORIZONTAL
            actionContainer.orientation = LinearLayout.HORIZONTAL
        } else {
            emptyStateContainer.orientation = LinearLayout.VERTICAL
            actionContainer.orientation = LinearLayout.VERTICAL
        }
    }

    enum class IllustrationType {
        EMPTY, NO_PERMISSIONS, NO_MEDIA, ERROR
    }
} 