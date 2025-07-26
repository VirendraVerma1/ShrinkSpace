package com.kreasaar.shrinkspace.di

import android.content.Context
import com.kreasaar.shrinkspace.data.*
import com.kreasaar.shrinkspace.media.MediaAccessManager
import com.kreasaar.shrinkspace.permissions.PermissionManager
import com.kreasaar.shrinkspace.utils.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideShrinkSpaceDatabase(@ApplicationContext context: Context): ShrinkSpaceDatabase {
        return ShrinkSpaceDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideMediaDao(database: ShrinkSpaceDatabase): MediaDao {
        return database.mediaDao()
    }

    @Provides
    @Singleton
    fun provideLogDao(database: ShrinkSpaceDatabase): LogDao {
        return database.logDao()
    }

    @Provides
    @Singleton
    fun provideJobDao(database: ShrinkSpaceDatabase): JobDao {
        return database.jobDao()
    }

    @Provides
    @Singleton
    fun provideMediaRepository(mediaDao: MediaDao): MediaRepository {
        return MediaRepository(mediaDao)
    }

    @Provides
    @Singleton
    fun provideLogRepository(logDao: LogDao): LogRepository {
        return LogRepository(logDao)
    }

    @Provides
    @Singleton
    fun provideJobRepository(jobDao: JobDao): JobRepository {
        return JobRepository(jobDao)
    }

    @Provides
    @Singleton
    fun provideMediaAccessManager(@ApplicationContext context: Context): MediaAccessManager {
        return MediaAccessManager(context)
    }

    @Provides
    @Singleton
    fun providePermissionManager(@ApplicationContext context: Context): PermissionManager {
        return PermissionManager(context)
    }

    @Provides
    @Singleton
    fun provideStorageUtils(@ApplicationContext context: Context): StorageUtils {
        return StorageUtils(context)
    }

    @Provides
    @Singleton
    fun provideFileUtils(@ApplicationContext context: Context): FileUtils {
        return FileUtils(context)
    }

    @Provides
    @Singleton
    fun provideCompressionUtils(@ApplicationContext context: Context): CompressionUtils {
        return CompressionUtils(context)
    }

    @Provides
    @Singleton
    fun provideAIUtils(@ApplicationContext context: Context): AIUtils {
        return AIUtils(context)
    }
} 