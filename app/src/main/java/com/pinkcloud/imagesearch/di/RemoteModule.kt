package com.pinkcloud.imagesearch.di

import com.pinkcloud.imagesearch.remote.ImageService
import com.pinkcloud.imagesearch.remote.ImageService.Companion.AUTHORIZATION_HEADER_NAME
import com.pinkcloud.imagesearch.remote.ImageService.Companion.AUTHORIZATION_HEADER_VALUE
import com.pinkcloud.imagesearch.remote.ImageService.Companion.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RemoteModule {

    @Singleton
    @Provides
    fun provideHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request()
                    .newBuilder()
                    .addHeader(AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE)
                    .build()
                chain.proceed(request)
            }
            .build()
    }

    @Singleton
    @Provides
    fun provideImageService(client: OkHttpClient): ImageService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ImageService::class.java)
    }
}