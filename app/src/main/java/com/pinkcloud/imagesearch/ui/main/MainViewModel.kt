package com.pinkcloud.imagesearch.ui.main

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import com.pinkcloud.imagesearch.data.Image
import com.pinkcloud.imagesearch.data.ImageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
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

    val filterList = MutableLiveData(mutableListOf(DEFAULT_FILTER))

    init {
        val initialSearch: String = savedStateHandle.get(LAST_SEARCH) ?: DEFAULT_SEARCH
        searchFlow.value = initialSearch
        val originPagingData = searchFlow
            .flatMapLatest { query ->
                repository.getSearchResultStream(query)
            }
            .cachedIn(viewModelScope)

        pagingDataFlow = filterFlow.flatMapLatest { filter ->
            originPagingData.map { pagingData ->
                pagingData.filter { image ->
                    updateFilterList(image.collection)
                    if (filter == DEFAULT_FILTER) true
                    else image.collection == filter
                }
            }
        }
    }

    private suspend fun updateFilterList(collection: String) {
        withContext(Dispatchers.Default) {
            if (!filterList.value!!.contains(collection)) {
                filterList.value?.add(collection)
                filterList.postValue(filterList.value)
            }
        }
    }

    // TODO MVVM
    fun search(text: String) {
        filterList.value?.also { filterList ->
            filterList.clear()
            filterList.add(DEFAULT_FILTER)
        }.run {
            filterList.value = this
        }
        searchFlow.value = text
    }

    override fun onCleared() {
        savedStateHandle[LAST_SEARCH] = searchFlow.value
        super.onCleared()
    }

    fun setFilter(filter: String) {
        filterFlow.value = filter
    }
}

private const val LAST_SEARCH = "last_search"
private const val LAST_FILTER = "last_filter"
private const val DEFAULT_SEARCH = "Android"
const val DEFAULT_FILTER = "all"