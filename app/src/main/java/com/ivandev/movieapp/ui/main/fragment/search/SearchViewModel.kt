package com.ivandev.movieapp.ui.main.fragment.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.ivandev.movieapp.core.paging.MoviePagingSource
import com.ivandev.movieapp.domain.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    private var query = ""
    private var moviePagingSource: MoviePagingSource? = null
        get() {
            if (field == null || field?.invalid == true) {
                field = MoviePagingSource(repository, query)
            }
            return field
        }

    val pager = Pager(
        PagingConfig(
            pageSize = 40,
            maxSize = 120,
            prefetchDistance = 40,
            initialLoadSize = 40,
            enablePlaceholders = false
        )
    ) {
        moviePagingSource!!
    }.flow.cachedIn(viewModelScope)

    fun searchByQuery(querySearch: String) {
        query = querySearch
        moviePagingSource?.invalidate()
    }
}