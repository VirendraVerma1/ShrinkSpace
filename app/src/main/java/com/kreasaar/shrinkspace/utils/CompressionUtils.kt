package com.kreasaar.shrinkspace.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMuxer
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class CompressionUtils @Inject constructor(
    private val appContext: Context
) {
    
    fun compressImage(
        inputUri: Uri,
        outputFile: File,
        quality: Int,
        maxWidth: Int = 1920,
        maxHeight: Int = 1080
    ): Boolean {
        return try {
            // Implement image compression using BitmapFactory and Bitmap.CompressFormat
            val inputStream = appContext.contentResolver.openInputStream(inputUri)
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            inputStream?.use { BitmapFactory.decodeStream(it, null, options) }
            
            // Calculate sample size
            val sampleSize = calculateSampleSize(options.outWidth, options.outHeight, maxWidth, maxHeight)
            
            // Decode with sample size
            val decodeOptions = BitmapFactory.Options().apply {
                inSampleSize = sampleSize
            }
            
            val inputStream2 = appContext.contentResolver.openInputStream(inputUri)
            val bitmap = inputStream2?.use { BitmapFactory.decodeStream(it, null, decodeOptions) }
            
            // Compress and save
            if (bitmap == null) return false
            FileOutputStream(outputFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
            }
            bitmap.recycle()
            
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    fun compressVideo(
        inputUri: Uri,
        outputFile: File,
        quality: Int
    ): Boolean {
        return try {
            // Implement video compression using MediaCodec or FFmpeg
            val extractor = MediaExtractor()
            extractor.setDataSource(appContext, inputUri, null)
            
            val muxer = MediaMuxer(outputFile.absolutePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
            
            // Find first video track and simply remux without re-encode (placeholder implementation)
            var videoTrackIndex = -1
            var format: MediaFormat? = null
            for (i in 0 until extractor.trackCount) {
                val f = extractor.getTrackFormat(i)
                val mime = f.getString(MediaFormat.KEY_MIME) ?: ""
                if (mime.startsWith("video/")) {
                    videoTrackIndex = i
                    format = f
                    break
                }
            }
            if (videoTrackIndex >= 0 && format != null) {
                extractor.selectTrack(videoTrackIndex)
                val outTrack = muxer.addTrack(format)
                muxer.start()
                val buffer = java.nio.ByteBuffer.allocate(256 * 1024)
                val bufferInfo = android.media.MediaCodec.BufferInfo()
                while (true) {
                    buffer.clear()
                    val sampleSize = extractor.readSampleData(buffer, 0)
                    if (sampleSize < 0) break
                    bufferInfo.offset = 0
                    bufferInfo.size = sampleSize
                    bufferInfo.presentationTimeUs = extractor.sampleTime
                    bufferInfo.flags = extractor.sampleFlags
                    muxer.writeSampleData(outTrack, buffer, bufferInfo)
                    extractor.advance()
                }
            }
            
            extractor.release()
            try { muxer.stop() } catch (_: Exception) {}
            try { muxer.release() } catch (_: Exception) {}
            
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    private fun calculateSampleSize(
        originalWidth: Int,
        originalHeight: Int,
        maxWidth: Int,
        maxHeight: Int
    ): Int {
        var sampleSize = 1
        while (originalWidth / (sampleSize * 2) >= maxWidth && 
               originalHeight / (sampleSize * 2) >= maxHeight) {
            sampleSize *= 2
        }
        return sampleSize
    }
    
    fun getCompressionQuality(quality: Int): String {
        return when {
            quality >= 90 -> "High"
            quality >= 70 -> "Good"
            quality >= 50 -> "Medium"
            else -> "Low"
        }
    }
    
    fun estimateCompressedSize(originalSize: Long, quality: Int): Long {
        val compressionRatio = when {
            quality >= 90 -> 0.8f
            quality >= 70 -> 0.6f
            quality >= 50 -> 0.4f
            else -> 0.2f
        }
        return (originalSize * compressionRatio).toLong()
    }
} 