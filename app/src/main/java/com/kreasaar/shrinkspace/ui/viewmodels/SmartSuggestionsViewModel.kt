package com.kreasaar.shrinkspace.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kreasaar.shrinkspace.data.MediaItem
import com.kreasaar.shrinkspace.data.MediaRepository
import com.kreasaar.shrinkspace.utils.AIUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SmartSuggestionsViewModel @Inject constructor(
    private val mediaRepository: MediaRepository,
    private val aiUtils: AIUtils
) : ViewModel() {

    private val _suggestions = MutableLiveData<List<MediaItem>>()
    val suggestions: LiveData<List<MediaItem>> = _suggestions

    private val _blurryImages = MutableLiveData<List<MediaItem>>()
    val blurryImages: LiveData<List<MediaItem>> = _blurryImages

    private val _duplicateGroups = MutableLiveData<List<List<MediaItem>>>()
    val duplicateGroups: LiveData<List<List<MediaItem>>> = _duplicateGroups

    private val _lowQualityImages = MutableLiveData<List<MediaItem>>()
    val lowQualityImages: LiveData<List<MediaItem>> = _lowQualityImages

    private val _isAnalyzing = MutableLiveData<Boolean>()
    val isAnalyzing: LiveData<Boolean> = _isAnalyzing

    fun analyzeMedia() {
        viewModelScope.launch {
            try {
                _isAnalyzing.value = true
                
                // Perform AI analysis of media
                val allMedia = mediaRepository.getAllMedia()
                val imageMedia = allMedia.filter { it.type == "image" }
                
                // Analyze each image for quality, blur, etc.
                val analyzedItems = mutableListOf<MediaItem>()
                val blurryItems = mutableListOf<MediaItem>()
                val lowQualityItems = mutableListOf<MediaItem>()
                
                imageMedia.forEach { mediaItem ->
                    // Convert MediaItem to File for AI analysis
                    val file = java.io.File(mediaItem.uri.path ?: "")
                    if (file.exists()) {
                        val bitmap = android.graphics.BitmapFactory.decodeFile(file.absolutePath)
                        if (bitmap != null) {
                            // Check for blur
                            if (aiUtils.detectBlur(bitmap)) {
                                blurryItems.add(mediaItem)
                            }
                            
                            // Check quality
                            val quality = aiUtils.analyzeImageQuality(bitmap)
                            if (quality < 0.5f) {
                                lowQualityItems.add(mediaItem)
                            }
                            
                            bitmap.recycle()
                        }
                    }
                }
                
                _blurryImages.value = blurryItems
                _lowQualityImages.value = lowQualityItems
                
                // Find duplicates
                val imageFiles = imageMedia.mapNotNull { 
                    java.io.File(it.uri.path ?: "").takeIf { file -> file.exists() }
                }
                val duplicateGroups = aiUtils.detectDuplicates(imageFiles)
                val duplicateItems = duplicateGroups.flatMap { files ->
                    files.mapNotNull { file ->
                        imageMedia.find { mediaItem -> 
                            mediaItem.uri.path == file.absolutePath 
                        }
                    }
                }
                _duplicateGroups.value = listOf(duplicateItems)
                
                // Combine all suggestions
                val allSuggestions = (blurryItems + lowQualityItems + duplicateItems).distinct()
                _suggestions.value = allSuggestions
                
                _isAnalyzing.value = false
            } catch (e: Exception) {
                _isAnalyzing.value = false
            }
        }
    }

    fun getBlurryImages(): List<MediaItem> {
        return _blurryImages.value ?: emptyList()
    }

    fun getDuplicateGroups(): List<List<MediaItem>> {
        return _duplicateGroups.value ?: emptyList()
    }

    fun getLowQualityImages(): List<MediaItem> {
        return _lowQualityImages.value ?: emptyList()
    }

    fun refreshSuggestions() {
        analyzeMedia()
    }
} 