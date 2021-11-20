package com.pinkcloud.imagesearch.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * separation of concerns.
 */
@Entity(tableName = "remote_keys")
data class RemoteKey(
    @PrimaryKey
    @ColumnInfo(name = "image_id")
    val imageId: String,
    val nextKey: Int?
)
