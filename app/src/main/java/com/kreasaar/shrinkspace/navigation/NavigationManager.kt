package com.kreasaar.shrinkspace.navigation

import androidx.navigation.NavController
import javax.inject.Inject

class NavigationManager @Inject constructor() {
    
    fun navigateToSplash(navController: NavController) {
        // Navigate to splash screen
        navController.navigate("splash") {
            popUpTo(navController.graph.startDestinationId) {
                inclusive = true
            }
        }
    }
    
    fun navigateToPermissions(navController: NavController) {
        // Navigate to permissions screen
        navController.navigate("permissions")
    }
    
    fun navigateToHome(navController: NavController) {
        // Navigate to home screen
        navController.navigate("home") {
            popUpTo("splash") { inclusive = true }
        }
    }
    
    fun navigateToGallery(navController: NavController) {
        // Navigate to gallery screen
        navController.navigate("gallery")
    }
    
    fun navigateToSmartSuggestions(navController: NavController) {
        // Navigate to smart suggestions screen
        navController.navigate("smart_suggestions")
    }
    
    fun navigateToCompressionOptions(navController: NavController) {
        // Navigate to compression options screen
        navController.navigate("compression_options")
    }
    
    fun navigateToBestPhotoPicker(navController: NavController) {
        // Navigate to best photo picker screen
        navController.navigate("best_photo_picker")
    }
    
    fun navigateToSettings(navController: NavController) {
        // Navigate to settings screen
        navController.navigate("settings")
    }
    
    fun navigateToReviewList(navController: NavController) {
        // Navigate to review list screen
        navController.navigate("review_list")
    }
    
    fun navigateToActivityLog(navController: NavController) {
        // Navigate to activity log screen
        navController.navigate("activity_log")
    }
    
    fun navigateToErrorState(navController: NavController, errorType: String) {
        // Navigate to error state screen with error type
        navController.navigate("error_state/$errorType")
    }
    
    fun goBack(navController: NavController) {
        // Navigate back
        navController.popBackStack()
    }
} 