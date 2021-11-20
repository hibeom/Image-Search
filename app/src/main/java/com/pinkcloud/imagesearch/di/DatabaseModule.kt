package com.pinkcloud.imagesearch.di

import android.content.Context
import androidx.room.Room
import com.pinkcloud.imagesearch.db.ImageDao
import com.pinkcloud.imagesearch.db.ImageDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideImageDao(database: ImageDatabase) = database.imageDao

    @Provides
    @Singleton
    fun provideRemoteKeyDao(database: ImageDatabase) = database.remoteKeyDao

    @Provides
    @Singleton
    fun provideImageDatabase(@ApplicationContext context: Context): ImageDatabase {
        return Room.databaseBuilder(
            context,
            ImageDatabase::class.java,
            "image_database"
        ).build()
    }
}