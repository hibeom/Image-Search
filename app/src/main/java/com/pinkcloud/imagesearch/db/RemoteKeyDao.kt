package com.pinkcloud.imagesearch.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RemoteKeyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<RemoteKey>)

    @Query("SELECT * FROM remote_keys WHERE image_id = :imageId")
    suspend fun remoteKeyImageId(imageId: String): RemoteKey?

    @Query("DELETE FROM remote_keys")
    suspend fun clearRemoteKey()
}