package com.pinkcloud.imagesearch.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.pinkcloud.imagesearch.db.ImageDatabase
import com.pinkcloud.imagesearch.remote.ImageService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageRepository @Inject constructor(
    private val service: ImageService,
    private val database: ImageDatabase
) {

    @OptIn(ExperimentalPagingApi::class)
    fun getSearchResultStream(query: String): Flow<PagingData<Image>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false,
                initialLoadSize = PAGE_SIZE
            ),
            pagingSourceFactory = { database.imageDao.getImages() },
            remoteMediator = ImageRemoteMediator(service, query, database)
        ).flow
    }
}