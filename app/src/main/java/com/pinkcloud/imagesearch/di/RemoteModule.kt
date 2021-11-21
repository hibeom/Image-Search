package com.pinkcloud.imagesearch.di

import com.pinkcloud.imagesearch.BuildConfig
import com.pinkcloud.imagesearch.remote.ImageService
import com.pinkcloud.imagesearch.remote.ImageService.Companion.AUTHORIZATION_HEADER_NAME
import com.pinkcloud.imagesearch.remote.ImageService.Companion.AUTHORIZATION_HEADER_VALUE
import com.pinkcloud.imagesearch.remote.ImageService.Companion.BASE_URL
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
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
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        val moshiConverterFactory = MoshiConverterFactory.create(moshi)

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(moshiConverterFactory)
            .build()
            .create(ImageService::class.java)
    }
}