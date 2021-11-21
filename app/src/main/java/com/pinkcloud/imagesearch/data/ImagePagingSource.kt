package com.pinkcloud.imagesearch.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.pinkcloud.imagesearch.remote.ImageService
import com.pinkcloud.imagesearch.remote.asImage
import com.squareup.moshi.JsonDataException
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception

private const val START_PAGE_INDEX = 1
const val PAGE_SIZE = 40

class ImagePagingSource(
    private val service: ImageService,
    private val query: String
): PagingSource<Int, Image>() {
    override fun getRefreshKey(state: PagingState<Int, Image>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Image> {
        val position = params.key ?: START_PAGE_INDEX
        return try {
            val response = service.getImages(query, position, params.loadSize)
            val images = response.documents.mapIndexed { index, document ->
                val id = "${query}_${position}_$index"
                document.asImage(id)
            }
            val nextKey = if (response.meta.isEnd || images.isEmpty() || position >= 50) {
                null
            } else {
                position + (params.loadSize / PAGE_SIZE)
            }
            LoadResult.Page(
                data = images,
                prevKey = if (position == START_PAGE_INDEX) null else position - 1,
                nextKey = nextKey
            )
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        } catch (exception: JsonDataException) {
            LoadResult.Page(
                data = listOf(),
                prevKey = if (position == START_PAGE_INDEX) null else position - 2,
                nextKey = if (position >= 50) null else position + (params.loadSize / PAGE_SIZE)
            )
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }
}