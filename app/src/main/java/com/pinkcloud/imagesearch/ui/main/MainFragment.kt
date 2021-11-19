package com.pinkcloud.imagesearch.ui.main

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import com.pinkcloud.imagesearch.databinding.MainFragmentBinding
import com.pinkcloud.imagesearch.util.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
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
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        setSearchInputListener()
        setImages()

        return binding.root
    }

    private fun setSearchInputListener() {
        binding.searchTextInput.apply {
            setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    hideKeyboard(context, this)
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
            setOnFocusChangeListener { view, hasFocus ->
                if (!hasFocus) hideKeyboard(context, view)
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
        binding.recyclerView.apply {
            addItemDecoration(
                DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL)
            )
            addItemDecoration(
                DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
            )
            // TODO footer not on grid layout
            this.adapter = adapter.withLoadStateFooter(
                footer = ImageLoadStateAdapter { adapter.retry() }
            )
        }

        lifecycleScope.launch {
            viewModel.pagingDataFlow
                .collectLatest { pagingData ->
                    adapter.submitData(pagingData)
                }
        }

        lifecycleScope.launch {
            adapter.loadStateFlow.collect { loadState ->
                val isListEmpty = loadState.refresh is LoadState.NotLoading && adapter.itemCount == 0
                binding.textEmpty.isVisible = isListEmpty
                binding.recyclerView.isVisible = !isListEmpty

                binding.loadingBar.isVisible = loadState.source.refresh is LoadState.Loading
                binding.retryButton.isVisible = loadState.source.refresh is LoadState.Error

                val errorState = loadState.source.append as? LoadState.Error
                    ?: loadState.source.prepend as? LoadState.Error
                    ?: loadState.append as? LoadState.Error
                    ?: loadState.prepend as? LoadState.Error
                errorState?.let {
                    Toast.makeText(
                        requireContext(),
                        "\uD83D\uDE28 Wooops ${it.error}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
        binding.retryButton.setOnClickListener { adapter.retry() }
    }


}