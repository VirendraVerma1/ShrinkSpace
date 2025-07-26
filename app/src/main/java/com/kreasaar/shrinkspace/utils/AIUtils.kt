package com.kreasaar.shrinkspace.utils

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.media.ExifInterface
import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest
import javax.inject.Inject

class AIUtils @Inject constructor() {
    
    fun analyzeImageQuality(bitmap: Bitmap): Float {
        // Implement image quality analysis using ML Kit or TensorFlow Lite
        val width = bitmap.width
        val height = bitmap.height
        var totalBrightness = 0f
        var totalContrast = 0f
        var totalSharpness = 0f
        
        // Calculate brightness
        for (x in 0 until width step 10) {
            for (y in 0 until height step 10) {
                val pixel = bitmap.getPixel(x, y)
                val brightness = (Color.red(pixel) + Color.green(pixel) + Color.blue(pixel)) / 3f
                totalBrightness += brightness
            }
        }
        val avgBrightness = totalBrightness / ((width * height) / 100f)
        
        // Calculate contrast (simplified)
        val contrast = calculateContrast(bitmap)
        
        // Calculate sharpness (simplified)
        val sharpness = calculateSharpness(bitmap)
        
        // Combine metrics for overall quality score
        val qualityScore = (avgBrightness * 0.3f + contrast * 0.4f + sharpness * 0.3f) / 255f
        return qualityScore.coerceIn(0f, 1f)
    }
    
    fun detectBlur(bitmap: Bitmap): Boolean {
        // Implement blur detection using computer vision
        val laplacianVariance = calculateLaplacianVariance(bitmap)
        return laplacianVariance < 100 // Threshold for blur detection
    }
    
    fun detectDuplicates(images: List<File>): List<List<File>> {
        // Implement duplicate detection using perceptual hashing
        val hashMap = mutableMapOf<String, MutableList<File>>()
        
        images.forEach { file ->
            val hash = calculatePerceptualHash(file)
            hashMap.getOrPut(hash) { mutableListOf() }.add(file)
        }
        
        return hashMap.values.filter { it.size > 1 }
    }
    
    fun selectBestPhoto(photos: List<File>): File? {
        // Implement best photo selection using quality metrics
        if (photos.isEmpty()) return null
        
        var bestPhoto = photos[0]
        var bestScore = 0f
        
        photos.forEach { photo ->
            val bitmap = BitmapFactory.decodeFile(photo.absolutePath)
            if (bitmap != null) {
                val qualityScore = analyzeImageQuality(bitmap)
                val isBlurry = detectBlur(bitmap)
                val finalScore = if (isBlurry) qualityScore * 0.5f else qualityScore
                
                if (finalScore > bestScore) {
                    bestScore = finalScore
                    bestPhoto = photo
                }
                bitmap.recycle()
            }
        }
        
        return bestPhoto
    }
    
    fun categorizeMedia(files: List<File>): Map<String, List<File>> {
        // Implement media categorization using ML
        val categories = mutableMapOf<String, MutableList<File>>()
        
        files.forEach { file ->
            val category = when (file.extension.lowercase()) {
                "jpg", "jpeg", "png", "gif", "bmp" -> "Images"
                "mp4", "avi", "mov", "mkv", "wmv" -> "Videos"
                "mp3", "wav", "aac", "flac" -> "Audio"
                else -> "Documents"
            }
            categories.getOrPut(category) { mutableListOf() }.add(file)
        }
        
        return categories
    }
    
    private fun calculateContrast(bitmap: Bitmap): Float {
        val width = bitmap.width
        val height = bitmap.height
        var minBrightness = 255f
        var maxBrightness = 0f
        
        for (x in 0 until width step 5) {
            for (y in 0 until height step 5) {
                val pixel = bitmap.getPixel(x, y)
                val brightness = (Color.red(pixel) + Color.green(pixel) + Color.blue(pixel)) / 3f
                minBrightness = minOf(minBrightness, brightness)
                maxBrightness = maxOf(maxBrightness, brightness)
            }
        }
        
        return maxBrightness - minBrightness
    }
    
    private fun calculateSharpness(bitmap: Bitmap): Float {
        val width = bitmap.width
        val height = bitmap.height
        var totalSharpness = 0f
        var count = 0
        
        for (x in 1 until width - 1) {
            for (y in 1 until height - 1) {
                val center = bitmap.getPixel(x, y)
                val left = bitmap.getPixel(x - 1, y)
                val right = bitmap.getPixel(x + 1, y)
                val top = bitmap.getPixel(x, y - 1)
                val bottom = bitmap.getPixel(x, y + 1)
                
                val horizontalDiff = Math.abs(Color.red(center) - Color.red(left)) +
                        Math.abs(Color.green(center) - Color.green(left)) +
                        Math.abs(Color.blue(center) - Color.blue(left))
                
                val verticalDiff = Math.abs(Color.red(center) - Color.red(top)) +
                        Math.abs(Color.green(center) - Color.green(top)) +
                        Math.abs(Color.blue(center) - Color.blue(top))
                
                totalSharpness += (horizontalDiff + verticalDiff) / 2f
                count++
            }
        }
        
        return if (count > 0) totalSharpness / count else 0f
    }
    
    private fun calculateLaplacianVariance(bitmap: Bitmap): Float {
        val width = bitmap.width
        val height = bitmap.height
        var totalVariance = 0f
        var count = 0
        
        for (x in 1 until width - 1) {
            for (y in 1 until height - 1) {
                val center = bitmap.getPixel(x, y)
                val left = bitmap.getPixel(x - 1, y)
                val right = bitmap.getPixel(x + 1, y)
                val top = bitmap.getPixel(x, y - 1)
                val bottom = bitmap.getPixel(x, y + 1)
                
                val laplacian = 4 * getBrightness(center) - getBrightness(left) - 
                        getBrightness(right) - getBrightness(top) - getBrightness(bottom)
                
                totalVariance += laplacian * laplacian
                count++
            }
        }
        
        return if (count > 0) totalVariance / count else 0f
    }
    
    private fun getBrightness(pixel: Int): Float {
        return (Color.red(pixel) + Color.green(pixel) + Color.blue(pixel)) / 3f
    }
    
    private fun calculatePerceptualHash(file: File): String {
        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
        if (bitmap == null) return ""
        
        // Resize to 8x8 for perceptual hash
        val resized = Bitmap.createScaledBitmap(bitmap, 8, 8, true)
        val pixels = IntArray(64)
        resized.getPixels(pixels, 0, 8, 0, 0, 8, 8)
        
        // Calculate average brightness
        var totalBrightness = 0f
        pixels.forEach { pixel ->
            totalBrightness += getBrightness(pixel)
        }
        val avgBrightness = totalBrightness / 64f
        
        // Create hash
        val hash = StringBuilder()
        pixels.forEach { pixel ->
            hash.append(if (getBrightness(pixel) > avgBrightness) "1" else "0")
        }
        
        bitmap.recycle()
        resized.recycle()
        
        return hash.toString()
    }
} 