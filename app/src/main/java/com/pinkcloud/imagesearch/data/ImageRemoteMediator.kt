package com.pinkcloud.imagesearch.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.pinkcloud.imagesearch.db.ImageDatabase
import com.pinkcloud.imagesearch.db.RemoteKey
import com.pinkcloud.imagesearch.remote.ImageService
import com.pinkcloud.imagesearch.remote.asImage
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import java.text.DecimalFormat

private const val START_PAGE_INDEX = 1
const val PAGE_SIZE = 80

@ExperimentalPagingApi
class ImageRemoteMediator(
    private val service: ImageService,
    private val query: String,
    private val imageDatabase: ImageDatabase
) : RemoteMediator<Int, Image>() {
    private val remoteKeyDao = imageDatabase.remoteKeyDao
    private val imageDao = imageDatabase.imageDao

    override suspend fun initialize(): InitializeAction {
        // Launch remote refresh as soon as paging starts and do not trigger remote prepend or
        // append until refresh has succeeded. In cases where we don't mind showing out-of-date,
        // cached offline data, we can return SKIP_INITIAL_REFRESH instead to prevent paging
        // triggering remote refresh.
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(loadType: LoadType, state: PagingState<Int, Image>): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKey = getRemoteKeyClosestToCurrentPosition(state)
                remoteKey?.nextKey?.minus(1) ?: START_PAGE_INDEX
            }
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> {
                val remoteKey = getRemoteKeyForLastItem(state)
                val nextKey = remoteKey?.nextKey
                if (nextKey == null) return MediatorResult.Success(endOfPaginationReached = remoteKey != null)
                nextKey
            }
        }

        try {
            val response = service.getImages(query, page, state.config.pageSize)
            val df = DecimalFormat("00")
            val images = response.documents.mapIndexed { index, document ->
                val id = "${query}_${df.format(page)}_${df.format(index)}"
                document.asImage(id)
            }
            val endOfPaginationReached = response.meta.isEnd || images.isEmpty()
            imageDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    imageDatabase.remoteKeyDao.clearRemoteKey()
                    imageDatabase.imageDao.clearAll()
                }
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = images.map {
                    RemoteKey(it.id, nextKey)
                }
                remoteKeyDao.insertAll(keys)
                imageDao.insertAll(images)
            }
            return MediatorResult.Success(endOfPaginationReached)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, Image>): RemoteKey? {
        // Get the last page that was retrieved, that contained items.
        // From that last page, get the last item
        return state.pages.lastOrNull() { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { image ->
                // Get the remote keys of the last item retrieved
                Timber.d("lastitem: ${image.id}")
                remoteKeyDao.remoteKeyImageId(image.id)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, Image>
    ): RemoteKey? {
        // The paging library is trying to load data after the anchor position
        // Get the item closest to the anchor position
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { imageId ->
                remoteKeyDao.remoteKeyImageId(imageId)
            }
        }
    }
}