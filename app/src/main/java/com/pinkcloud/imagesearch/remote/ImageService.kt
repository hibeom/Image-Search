package com.pinkcloud.imagesearch.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ImageService {

    // Retrofit's Coroutine CallAdapter dispatches on a worker thread.
    @GET("v2/search/image")
    suspend fun getImages(
        @Query("query") search: String = "android",
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 80
    ): ImageResponse
}