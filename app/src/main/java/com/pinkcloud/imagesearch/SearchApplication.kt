package com.pinkcloud.imagesearch

import android.app.Application
import com.pinkcloud.imagesearch.util.ImageLoader
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@HiltAndroidApp
class SearchApplication: Application() {

    override fun onCreate() {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
        ImageLoader.coroutineScope = scope
        super.onCreate()
    }
}