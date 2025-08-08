package com.kreasaar.shrinkspace.data

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "media_items")
data class MediaItem(
    @PrimaryKey val id: Long,
    val name: String,
    val uri: Uri,
    val type: String,
    val size: Long,
    val dateAdded: Long,
    val path: String
) 