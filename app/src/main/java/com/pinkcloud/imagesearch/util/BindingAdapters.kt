package com.pinkcloud.imagesearch.util

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.pinkcloud.imagesearch.data.Image

@BindingAdapter("image", "placeHolder", requireAll = false)
fun setImage(imageView: ImageView, image: Image, placeHolder: Drawable?) {
    image.thumbnailUrl?.let { url ->
        ImageLoader.loadBitmap(url, imageView, placeHolder)
    }
}