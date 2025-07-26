package com.kreasaar.shrinkspace

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ShrinkSpaceApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        initializeApp()
    }
    
    private fun initializeApp() {
        initializeCrashReporting()
        initializeAnalytics()
        initializeWorkManager()
        initializeDatabase()
    }
    
    private fun initializeCrashReporting() {
        // Add crash reporting library like Firebase Crashlytics
        // Implementation for crash reporting
        try {
            // FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
            // FirebaseCrashlytics.getInstance().setCustomKey("app_version", BuildConfig.VERSION_NAME)
            // FirebaseCrashlytics.getInstance().setCustomKey("build_type", BuildConfig.BUILD_TYPE)
        } catch (e: Exception) {
            // Handle crash reporting initialization error
            e.printStackTrace()
        }
    }
    
    private fun initializeAnalytics() {
        // Add analytics library like Firebase Analytics
        // Implementation for analytics
        try {
            // val firebaseAnalytics = FirebaseAnalytics.getInstance(this)
            // firebaseAnalytics.setAnalyticsCollectionEnabled(true)
            // firebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, null)
        } catch (e: Exception) {
            // Handle analytics initialization error
            e.printStackTrace()
        }
    }
    
    private fun initializeWorkManager() {
        // WorkManager is automatically initialized by Hilt
        // Additional configuration can be added here if needed
    }
    
    private fun initializeDatabase() {
        // Room database is automatically initialized by Hilt
        // Additional configuration can be added here if needed
    }
} 