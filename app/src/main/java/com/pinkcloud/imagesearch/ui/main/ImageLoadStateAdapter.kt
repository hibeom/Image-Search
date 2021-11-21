package com.pinkcloud.imagesearch.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pinkcloud.imagesearch.databinding.LoadStateFooterViewItemBinding

class ImageLoadStateAdapter(private val retry: () -> Unit): LoadStateAdapter<ImageLoadStateAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): ViewHolder {
        return ViewHolder.create(parent, retry)
    }

    class ViewHolder(
        private val binding: LoadStateFooterViewItemBinding,
        retry: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.retryButton.setOnClickListener { retry.invoke() }
        }

        fun bind(loadState: LoadState) {
            binding.loadState = loadState
        }

        companion object {
            fun create(parent: ViewGroup, retry: () -> Unit): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = LoadStateFooterViewItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding, retry)
            }
        }
    }

}