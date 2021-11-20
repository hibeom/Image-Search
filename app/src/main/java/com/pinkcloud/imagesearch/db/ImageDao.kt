package com.pinkcloud.imagesearch.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pinkcloud.imagesearch.data.Image

@Dao
interface ImageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(images: List<Image>)

    @Query("SELECT * FROM images")
    fun getImages(): PagingSource<Int, Image>

    @Query("DELETE FROM images")
    suspend fun clearAll()
}