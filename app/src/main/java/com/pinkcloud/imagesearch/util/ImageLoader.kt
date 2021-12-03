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
//        imageCache.get(url) ?: run {
//            load(url)
//        }?.let { bitmap ->
//            imageView.setImageBitmap(bitmap)
//        }
        val bitmap = imageCache.get(url) ?: run {
            load(url)
        }
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap)
        } else {
            imageView.setImageDrawable(placeHolder)
        }
    }

    fun preload(url: String) {
        imageCache.get(url) ?: run {
            load(url)
        }
    }

    private fun load(url: String): Bitmap? {
        return runBlocking {
            val deferred = coroutineScope.async(Dispatchers.IO) {
                BitmapFactory.decodeStream(URL(url).openStream()).also { bitmap ->
                    imageCache.put(url, bitmap)
                }
            }
            try {
                deferred.await()
            } catch (exception: Exception) {
                null
            }
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