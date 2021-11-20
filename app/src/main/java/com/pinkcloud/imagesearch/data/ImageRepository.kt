package com.pinkcloud.imagesearch.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.pinkcloud.imagesearch.remote.ImageService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageRepository @Inject constructor(
    private val service: ImageService
) {

    fun getSearchResultStream(query: String): Flow<PagingData<Image>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false,
                initialLoadSize = PAGE_SIZE * 2
            ),
            pagingSourceFactory = { ImagePagingSource(service, query) }
        ).flow
    }
}