package com.pinkcloud.imagesearch.util

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.pinkcloud.imagesearch.data.Image

@BindingAdapter("image")
fun setImage(imageView: ImageView, image: Image) {
    // TODO run without global scope
    image.thumbnailUrl?.let { url ->
        ImageLoader.loadBitmap(url, imageView)
    }
}