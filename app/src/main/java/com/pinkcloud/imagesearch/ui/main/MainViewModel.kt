package com.pinkcloud.imagesearch.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import com.pinkcloud.imagesearch.data.Image
import com.pinkcloud.imagesearch.data.ImageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: ImageRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val searchState = MutableStateFlow(DEFAULT_SEARCH)
    private val filterState = MutableStateFlow(DEFAULT_FILTER)

    val pagingDataFlow: Flow<PagingData<Image>>

    val filterList = MutableLiveData(mutableListOf(DEFAULT_FILTER))

    init {
        val initialSearch: String = savedStateHandle.get(LAST_SEARCH) ?: DEFAULT_SEARCH
        searchState.value = initialSearch
        val originPagingData = searchState
            .flatMapLatest { query ->
                repository.getSearchResultStream(query)
            }
            .cachedIn(viewModelScope)

        pagingDataFlow = filterState.flatMapLatest { filter ->
            originPagingData.map { pagingData ->
                pagingData.filter { image ->
                    updateFilterList(image.collection)
                    if (filter == DEFAULT_FILTER) true
                    else image.collection == filter
                }
            }
        }
    }

    private suspend fun updateFilterList(collection: String?) {
        withContext(Dispatchers.Default) {
            collection?.let {
                if (!filterList.value!!.contains(collection)) {
                    filterList.value?.add(collection)
                    filterList.postValue(filterList.value)
                }
            }
        }
    }

    fun search(text: String) {
        filterList.value = filterList.value?.also { filterList ->
            filterList.clear()
            filterList.add(DEFAULT_FILTER)
        }
        searchState.value = text
    }

    override fun onCleared() {
        savedStateHandle[LAST_SEARCH] = searchState.value
        super.onCleared()
    }

    fun setFilter(filter: String) {
        filterState.value = filter
    }
}

private const val LAST_SEARCH = "last_search"
private const val DEFAULT_SEARCH = "Android"
const val DEFAULT_FILTER = "all"