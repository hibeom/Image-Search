package com.pinkcloud.imagesearch.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pinkcloud.imagesearch.data.Image

@Database(entities = [Image::class, RemoteKey::class], version = 1, exportSchema = false)
abstract class ImageDatabase: RoomDatabase() {

    abstract val imageDao: ImageDao
    abstract val remoteKeyDao: RemoteKeyDao
}