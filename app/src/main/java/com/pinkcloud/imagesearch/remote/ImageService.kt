package com.pinkcloud.imagesearch.remote

import com.pinkcloud.imagesearch.BuildConfig
import retrofit2.http.GET
import retrofit2.http.Query

interface ImageService {

    @GET("v2/search/image")
    suspend fun getImages(
        @Query("query") search: String = "android",
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 80
    ): ImageResponse

    companion object {
        const val BASE_URL = "https://dapi.kakao.com/"
        const val AUTHORIZATION_HEADER_NAME = "Authorization"
        const val AUTHORIZATION_HEADER_VALUE = "KakaoAK ${BuildConfig.REST_API_KEY}"
    }
}