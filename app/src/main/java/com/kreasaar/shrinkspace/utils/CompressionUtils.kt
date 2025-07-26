package com.kreasaar.shrinkspace.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaMuxer
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class CompressionUtils @Inject constructor() {
    
    fun compressImage(
        inputUri: Uri,
        outputFile: File,
        quality: Int,
        maxWidth: Int = 1920,
        maxHeight: Int = 1080
    ): Boolean {
        return try {
            // Implement image compression using BitmapFactory and Bitmap.CompressFormat
            val inputStream = inputUri.toFile().inputStream()
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeStream(inputStream, null, options)
            inputStream.close()
            
            // Calculate sample size
            val sampleSize = calculateSampleSize(options.outWidth, options.outHeight, maxWidth, maxHeight)
            
            // Decode with sample size
            val decodeOptions = BitmapFactory.Options().apply {
                inSampleSize = sampleSize
            }
            
            val inputStream2 = inputUri.toFile().inputStream()
            val bitmap = BitmapFactory.decodeStream(inputStream2, null, decodeOptions)
            inputStream2.close()
            
            // Compress and save
            val outputStream = FileOutputStream(outputFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            outputStream.close()
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
            extractor.setDataSource(inputUri.toString())
            
            val muxer = MediaMuxer(outputFile.absolutePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
            
            // Find video track
            var videoTrackIndex = -1
            for (i in 0 until extractor.trackCount) {
                val format = extractor.getTrackFormat(i)
                val mime = format.getString(MediaCodec.KEY_MIME)
                if (mime.startsWith("video/")) {
                    videoTrackIndex = i
                    break
                }
            }
            
            if (videoTrackIndex >= 0) {
                extractor.selectTrack(videoTrackIndex)
                val videoFormat = extractor.getTrackFormat(videoTrackIndex)
                
                // Create MediaCodec for video compression
                val codec = MediaCodec.createEncoderByType(videoFormat.getString(MediaCodec.KEY_MIME))
                codec.configure(videoFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
                codec.start()
                
                // Process video frames
                val bufferInfo = MediaCodec.BufferInfo()
                var outputTrackIndex = -1
                
                while (true) {
                    val inputBufferId = codec.dequeueInputBuffer(-1)
                    if (inputBufferId >= 0) {
                        val inputBuffer = codec.getInputBuffer(inputBufferId)
                        val sampleSize = extractor.readSampleData(inputBuffer, 0)
                        
                        if (sampleSize < 0) {
                            codec.queueInputBuffer(inputBufferId, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM)
                            break
                        } else {
                            codec.queueInputBuffer(inputBufferId, 0, sampleSize, extractor.sampleTime, 0)
                            extractor.advance()
                        }
                    }
                    
                    val outputBufferId = codec.dequeueOutputBuffer(bufferInfo, 0)
                    if (outputBufferId >= 0) {
                        val outputBuffer = codec.getOutputBuffer(outputBufferId)
                        if (outputTrackIndex == -1) {
                            outputTrackIndex = muxer.addTrack(codec.outputFormat)
                            muxer.start()
                        }
                        muxer.writeSampleData(outputTrackIndex, outputBuffer, bufferInfo)
                        codec.releaseOutputBuffer(outputBufferId, false)
                    }
                }
                
                codec.stop()
                codec.release()
            }
            
            extractor.release()
            muxer.stop()
            muxer.release()
            
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