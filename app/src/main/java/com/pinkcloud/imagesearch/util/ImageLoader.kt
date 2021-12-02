package com.pinkcloud.imagesearch.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.util.LruCache
import android.widget.ImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.net.URL

object ImageLoader {
    private val imageCache: LruCache<String, Bitmap>

    suspend fun loadBitmap(url: String, imageView: ImageView) {
        imageCache.get(url) ?: run {
            load(url)
        }?.let { bitmap ->
            imageView.setImageBitmap(bitmap)
        }
    }

    suspend fun preload(url: String) {
        imageCache.get(url) ?: run {
            load(url)
        }
    }

    private suspend fun load(url: String): Bitmap? = withContext(Dispatchers.IO) {
        try {
            BitmapFactory.decodeStream(URL(url).openStream()).also { bitmap ->
                imageCache.put(url, bitmap)
            }
        } catch (exception: Exception) {
            null
        }
    }

    init {
        val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
        val cacheSize = maxMemory / 8
        imageCache = object : LruCache<String, Bitmap>(cacheSize) {
            override fun sizeOf(key: String, value: Bitmap): Int {
                return value.byteCount / 1024
            }
        }
    }
}