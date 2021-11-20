package com.pinkcloud.imagesearch.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "images")
data class Image(
    @PrimaryKey
    val id: String,
    val collection: String,
    @ColumnInfo(name = "thumb_nail")
    val thumbnailUrl: String,
    val width: Int,
    val height: Int
)
