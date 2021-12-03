package com.pinkcloud.imagesearch.ui.main

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.pinkcloud.imagesearch.data.Image
import com.pinkcloud.imagesearch.databinding.ListItemImageBinding
import com.pinkcloud.imagesearch.util.ImageLoader

class ImagesAdapter(
    private val spanCount: Int,
    private val context: Context) :
    PagingDataAdapter<Image, ImagesAdapter.ViewHolder>(ImageDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val image = getItem(position)
        image?.let {
            holder.bind(it)
        }
        preload(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder(private val binding: ListItemImageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(image: Image) {
            binding.image = image
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemImageBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    private fun preload(position: Int) {
        var endPosition = position + spanCount * 6
        if (endPosition > itemCount) endPosition = itemCount

        for (i in position until endPosition) {
            val image = getItem(i)
            image?.thumbnailUrl?.let { url ->
                ImageLoader.preload(url)
            }
        }
    }
}

class ImageDiffCallback : DiffUtil.ItemCallback<Image>() {
    override fun areItemsTheSame(oldItem: Image, newItem: Image): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Image, newItem: Image): Boolean {
        return oldItem == newItem
    }
}