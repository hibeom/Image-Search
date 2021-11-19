package com.pinkcloud.imagesearch.ui.main

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.pinkcloud.imagesearch.data.Image
import com.pinkcloud.imagesearch.data.ImageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: ImageRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val searchFlow = MutableStateFlow(DEFAULT_SEARCH)
    val search: LiveData<String>
        get() = searchFlow.asLiveData()
    private val filterFlow = MutableStateFlow(DEFAULT_FILTER)
    val filter: LiveData<String>
        get() = filterFlow.asLiveData()

    val pagingDataFlow: Flow<PagingData<Image>>

    init {
        val initialSearch: String = savedStateHandle.get(LAST_SEARCH) ?: DEFAULT_SEARCH
        searchFlow.value = initialSearch
        pagingDataFlow = searchFlow
            .flatMapLatest { query ->
                repository.getSearchResultStream(query)
            }.cachedIn(viewModelScope)
    }

    fun search(text: String) {
        searchFlow.value = text
    }

    override fun onCleared() {
        savedStateHandle[LAST_SEARCH] = searchFlow.value
        super.onCleared()
    }
}

private const val LAST_SEARCH = "last_search"
private const val LAST_FILTER = "last_filter"
private const val DEFAULT_SEARCH = "Android"
private const val DEFAULT_FILTER = "all"