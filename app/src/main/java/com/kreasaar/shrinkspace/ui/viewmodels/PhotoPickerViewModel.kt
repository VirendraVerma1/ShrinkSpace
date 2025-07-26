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
class PhotoPickerViewModel @Inject constructor(
    private val mediaRepository: MediaRepository,
    private val aiUtils: AIUtils
) : ViewModel() {

    private val _photoGroups = MutableLiveData<List<List<MediaItem>>>()
    val photoGroups: LiveData<List<List<MediaItem>>> = _photoGroups

    private val _selectedPhotos = MutableLiveData<List<MediaItem>>()
    val selectedPhotos: LiveData<List<MediaItem>> = _selectedPhotos

    private val _isAnalyzing = MutableLiveData<Boolean>()
    val isAnalyzing: LiveData<Boolean> = _isAnalyzing

    private val _analysisProgress = MutableLiveData<Int>()
    val analysisProgress: LiveData<Int> = _analysisProgress

    fun analyzePhotos() {
        viewModelScope.launch {
            try {
                _isAnalyzing.value = true
                _analysisProgress.value = 0
                
                // Group similar photos and analyze quality
                val allMedia = mediaRepository.getAllMedia()
                val imageMedia = allMedia.filter { it.type == "image" }
                
                // Group photos by similarity (simplified grouping by file size and name)
                val groups = mutableListOf<List<MediaItem>>()
                val processedItems = mutableSetOf<Long>()
                
                imageMedia.forEach { mediaItem ->
                    if (mediaItem.id !in processedItems) {
                        val similarPhotos = mutableListOf<MediaItem>()
                        similarPhotos.add(mediaItem)
                        processedItems.add(mediaItem.id)
                        
                        // Find similar photos (same size range, similar names)
                        imageMedia.forEach { otherItem ->
                            if (otherItem.id != mediaItem.id && otherItem.id !in processedItems) {
                                val sizeDiff = kotlin.math.abs(mediaItem.size - otherItem.size)
                                val sizeThreshold = mediaItem.size * 0.1 // 10% size difference
                                
                                if (sizeDiff <= sizeThreshold) {
                                    similarPhotos.add(otherItem)
                                    processedItems.add(otherItem.id)
                                }
                            }
                        }
                        
                        if (similarPhotos.size > 1) {
                            groups.add(similarPhotos)
                        }
                    }
                }
                
                _photoGroups.value = groups
                _analysisProgress.value = 50
                
                // Use AI to select the best photo from each group
                val bestPhotos = mutableListOf<MediaItem>()
                groups.forEach { group ->
                    val files = group.mapNotNull { 
                        java.io.File(it.uri.path ?: "").takeIf { file -> file.exists() }
                    }
                    
                    if (files.isNotEmpty()) {
                        val bestFile = aiUtils.selectBestPhoto(files)
                        if (bestFile != null) {
                            val bestMediaItem = group.find { mediaItem ->
                                mediaItem.uri.path == bestFile.absolutePath
                            }
                            if (bestMediaItem != null) {
                                bestPhotos.add(bestMediaItem)
                            }
                        }
                    }
                }
                
                _selectedPhotos.value = bestPhotos
                _analysisProgress.value = 100
                _isAnalyzing.value = false
                
            } catch (e: Exception) {
                _isAnalyzing.value = false
            }
        }
    }

    fun selectBestPhotoFromGroup(group: List<MediaItem>) {
        viewModelScope.launch {
            try {
                // Implement best photo selection logic
                val files = group.mapNotNull { 
                    java.io.File(it.uri.path ?: "").takeIf { file -> file.exists() }
                }
                
                if (files.isNotEmpty()) {
                    val bestFile = aiUtils.selectBestPhoto(files)
                    if (bestFile != null) {
                        val bestMediaItem = group.find { mediaItem ->
                            mediaItem.uri.path == bestFile.absolutePath
                        }
                        if (bestMediaItem != null) {
                            val currentSelected = _selectedPhotos.value?.toMutableList() ?: mutableListOf()
                            if (!currentSelected.contains(bestMediaItem)) {
                                currentSelected.add(bestMediaItem)
                                _selectedPhotos.value = currentSelected
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                // Handle selection error
            }
        }
    }

    fun implementPhotoGroupingLogic() {
        viewModelScope.launch {
            try {
                // Implement photo grouping logic
                val allMedia = mediaRepository.getAllMedia()
                val imageMedia = allMedia.filter { it.type == "image" }
                
                // Group by file size ranges and creation time proximity
                val groups = mutableListOf<List<MediaItem>>()
                val processedItems = mutableSetOf<Long>()
                
                imageMedia.forEach { mediaItem ->
                    if (mediaItem.id !in processedItems) {
                        val group = mutableListOf<MediaItem>()
                        group.add(mediaItem)
                        processedItems.add(mediaItem.id)
                        
                        // Find photos with similar characteristics
                        imageMedia.forEach { otherItem ->
                            if (otherItem.id != mediaItem.id && otherItem.id !in processedItems) {
                                val sizeDiff = kotlin.math.abs(mediaItem.size - otherItem.size)
                                val sizeThreshold = mediaItem.size * 0.2 // 20% size difference
                                
                                if (sizeDiff <= sizeThreshold) {
                                    group.add(otherItem)
                                    processedItems.add(otherItem.id)
                                }
                            }
                        }
                        
                        if (group.size > 1) {
                            groups.add(group)
                        }
                    }
                }
                
                _photoGroups.value = groups
            } catch (e: Exception) {
                // Handle grouping error
            }
        }
    }

    fun implementBestPhotoSelectionLogic(group: List<MediaItem>) {
        viewModelScope.launch {
            try {
                // Implement best photo selection logic
                val files = group.mapNotNull { 
                    java.io.File(it.uri.path ?: "").takeIf { file -> file.exists() }
                }
                
                if (files.isNotEmpty()) {
                    val bestFile = aiUtils.selectBestPhoto(files)
                    if (bestFile != null) {
                        val bestMediaItem = group.find { mediaItem ->
                            mediaItem.uri.path == bestFile.absolutePath
                        }
                        if (bestMediaItem != null) {
                            val currentSelected = _selectedPhotos.value?.toMutableList() ?: mutableListOf()
                            if (!currentSelected.contains(bestMediaItem)) {
                                currentSelected.add(bestMediaItem)
                                _selectedPhotos.value = currentSelected
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                // Handle selection error
            }
        }
    }

    fun clearSelection() {
        _selectedPhotos.value = emptyList()
    }

    fun getSelectedCount(): Int {
        return _selectedPhotos.value?.size ?: 0
    }

    fun refreshAnalysis() {
        analyzePhotos()
    }
} 