package com.pinkcloud.imagesearch.util

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.pinkcloud.imagesearch.R

@BindingAdapter("imageUrl")
fun setImage(imageView: ImageView, url: String) {
    Glide.with(imageView.context)
        .load(url)
        .placeholder(R.drawable.ic_placeholder)
        .centerCrop()
        .into(imageView)
}