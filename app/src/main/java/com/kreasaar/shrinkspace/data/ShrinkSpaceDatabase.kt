package com.kreasaar.shrinkspace.data

import android.content.Context
import androidx.room.Database
import androidx.room.TypeConverters
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [MediaItem::class, LogEntry::class, JobState::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(UriConverters::class)
abstract class ShrinkSpaceDatabase : RoomDatabase() {
    abstract fun mediaDao(): MediaDao
    abstract fun logDao(): LogDao
    abstract fun jobDao(): JobDao

    companion object {
        @Volatile
        private var INSTANCE: ShrinkSpaceDatabase? = null

        fun getDatabase(context: Context): ShrinkSpaceDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ShrinkSpaceDatabase::class.java,
                    "shrinkspace_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
} 