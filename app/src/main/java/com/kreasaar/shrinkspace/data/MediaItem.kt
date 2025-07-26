package com.kreasaar.shrinkspace.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "media_items")
data class MediaItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val uri: String,
    val path: String,
    val type: String,
    val date: Long,
    val size: Long,
    val status: String
) 