package com.pinkcloud.imagesearch.util

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.pinkcloud.imagesearch.R
import com.pinkcloud.imagesearch.data.Image

@BindingAdapter("image")
fun setImage(imageView: ImageView, image: Image) {
    image.run {
        Glide.with(imageView.context)
            .load(thumbnailUrl)
            .placeholder(R.color.gray)
            .centerCrop()
            .signature(ObjectKey(datetime ?: thumbnailUrl ?: id))
            .into(imageView)
    }
}