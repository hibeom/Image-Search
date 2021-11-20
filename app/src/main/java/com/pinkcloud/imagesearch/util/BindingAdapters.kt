package com.pinkcloud.imagesearch.util

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.pinkcloud.imagesearch.R

@BindingAdapter("imageUrl", "datetime")
fun setImage(imageView: ImageView, url: String, datetime: String) {
    Glide.with(imageView.context)
        .load(url)
        .placeholder(R.color.gray)
        .centerCrop()
        .signature(ObjectKey(datetime))
        .into(imageView)
}