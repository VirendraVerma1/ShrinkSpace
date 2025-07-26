package com.kreasaar.shrinkspace.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kreasaar.shrinkspace.data.MediaItem
import com.kreasaar.shrinkspace.data.MediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val mediaRepository: MediaRepository
) : ViewModel() {
    
    private val _galleryItems = MutableLiveData<List<MediaItem>>()
    val galleryItems: LiveData<List<MediaItem>> = _galleryItems
    
    private val _selectedItems = MutableLiveData<Set<Long>>()
    val selectedItems: LiveData<Set<Long>> = _selectedItems
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    init {
        loadGalleryItems()
    }
    
    private fun loadGalleryItems() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                
                // Load gallery items from repository
                val items = mediaRepository.getAllMedia()
                _galleryItems.value = items
                
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }
    
    fun refreshGallery() {
        loadGalleryItems()
    }
    
    fun toggleItemSelection(itemId: Long) {
        val currentSelected = _selectedItems.value?.toMutableSet() ?: mutableSetOf()
        if (currentSelected.contains(itemId)) {
            currentSelected.remove(itemId)
        } else {
            currentSelected.add(itemId)
        }
        _selectedItems.value = currentSelected
    }
    
    fun selectAll() {
        val allIds = _galleryItems.value?.map { it.id }?.toSet() ?: emptySet()
        _selectedItems.value = allIds
    }
    
    fun clearSelection() {
        _selectedItems.value = emptySet()
    }
    
    fun getSelectedItems(): List<MediaItem> {
        val selectedIds = _selectedItems.value ?: emptySet()
        return _galleryItems.value?.filter { it.id in selectedIds } ?: emptyList()
    }
    
    fun getSelectedCount(): Int {
        return _selectedItems.value?.size ?: 0
    }
    
    fun isItemSelected(itemId: Long): Boolean {
        return _selectedItems.value?.contains(itemId) ?: false
    }
    
    fun filterByType(type: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                
                val allItems = mediaRepository.getAllMedia()
                val filteredItems = if (type == "all") {
                    allItems
                } else {
                    allItems.filter { it.type == type }
                }
                
                _galleryItems.value = filteredItems
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }
    
    fun searchItems(query: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                
                val allItems = mediaRepository.getAllMedia()
                val searchResults = if (query.isBlank()) {
                    allItems
                } else {
                    allItems.filter { 
                        it.name.contains(query, ignoreCase = true) ||
                        it.type.contains(query, ignoreCase = true)
                    }
                }
                
                _galleryItems.value = searchResults
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }
    
    fun clearError() {
        _error.value = null
    }
} 