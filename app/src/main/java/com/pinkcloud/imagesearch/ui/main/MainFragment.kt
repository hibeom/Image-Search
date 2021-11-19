package com.pinkcloud.imagesearch.ui.main

import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.pinkcloud.imagesearch.R
import com.pinkcloud.imagesearch.databinding.MainFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var binding: MainFragmentBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MainFragmentBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel

        setSearchInputListener()
        setImages()

        return binding.root
    }

    private fun setSearchInputListener() {
        binding.searchTextInput.apply {
            setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    updateImagesFromInput(text.toString())
                    true
                } else false
            }
            setOnKeyListener { _, keyCode, keyEvent ->
                if (keyEvent.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    updateImagesFromInput(text.toString())
                    true
                } else false
            }
        }
    }

    private fun updateImagesFromInput(searchText: String) {
        searchText.trim().let {
            if (it.isNotEmpty()) {
                binding.recyclerView.scrollToPosition(0)
                viewModel.search(searchText)
            }
        }
    }

    private fun setImages() {
        val adapter = ImagesAdapter()
        binding.recyclerView.adapter = adapter

        lifecycleScope.launch {
            viewModel.pagingDataFlow
                .collectLatest { pagingData ->
                    adapter.submitData(pagingData)
                }
        }
    }
}