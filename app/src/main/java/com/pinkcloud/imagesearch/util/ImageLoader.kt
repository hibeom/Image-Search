package com.pinkcloud.imagesearch.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.util.Log
import android.util.LruCache
import android.widget.ImageView
import kotlinx.coroutines.*
import java.lang.Exception
import java.net.URL

object ImageLoader {
    private val imageCache: LruCache<String, Bitmap>
    lateinit var coroutineScope: CoroutineScope

    fun loadBitmap(url: String, imageView: ImageView, placeHolder: Drawable?) {
        imageView.setImageDrawable(placeHolder)
        coroutineScope.launch {
            val bitmap = imageCache.get(url) ?: run {
                load(url)
            }
            bitmap?.let {
                imageView.setImageBitmap(it)
            }
        }
//        imageCache.get(url) ?: run {
//            load(url)
//        }?.let { bitmap ->
//            imageView.setImageBitmap(bitmap)
//        }

    }

    fun preload(url: String) {
        coroutineScope.launch {
            imageCache.get(url) ?: run {
                load(url)
            }
        }
    }

    private suspend fun load(url: String): Bitmap? {
        val deferred = coroutineScope.async(Dispatchers.IO) {
            BitmapFactory.decodeStream(URL(url).openStream()).also { bitmap ->
                imageCache.put(url, bitmap)
            }
        }
        return try {
            deferred.await()
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