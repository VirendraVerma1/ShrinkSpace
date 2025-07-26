package com.kreasaar.shrinkspace.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.airbnb.lottie.LottieAnimationView
import com.kreasaar.shrinkspace.R

class LottiePlaceholderView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    
    private val lottieAnimationView: LottieAnimationView
    
    init {
        // Integrate Lottie animation here using LottieAnimationView
        LayoutInflater.from(context).inflate(R.layout.view_lottie_placeholder, this, true)
        lottieAnimationView = findViewById(R.id.lottie_animation_view)
    }
    
    fun setAnimation(animationResId: Int) {
        lottieAnimationView.setAnimation(animationResId)
    }
    
    fun setAnimation(animationName: String) {
        lottieAnimationView.setAnimation(animationName)
    }
    
    fun playAnimation() {
        lottieAnimationView.playAnimation()
    }
    
    fun pauseAnimation() {
        lottieAnimationView.pauseAnimation()
    }
    
    fun stopAnimation() {
        lottieAnimationView.cancelAnimation()
    }
    
    fun setRepeatCount(count: Int) {
        lottieAnimationView.repeatCount = count
    }
    
    fun setRepeatMode(mode: Int) {
        lottieAnimationView.repeatMode = mode
    }
    
    fun setSpeed(speed: Float) {
        lottieAnimationView.speed = speed
    }
    
    fun isAnimating(): Boolean {
        return lottieAnimationView.isAnimating
    }
    
    fun setProgress(progress: Float) {
        lottieAnimationView.progress = progress
    }
    
    fun getProgress(): Float {
        return lottieAnimationView.progress
    }
} 