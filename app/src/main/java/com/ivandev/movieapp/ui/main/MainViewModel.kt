package com.ivandev.movieapp.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.ivandev.movieapp.core.paging.MoviePagingSource
import com.ivandev.movieapp.domain.Repository
import com.ivandev.movieapp.domain.model.ResultModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: Repository) : ViewModel() {
    private val _repositoryResponseReady = MutableLiveData<Boolean>()
    val repositoryResponseReady: LiveData<Boolean>
        get() = _repositoryResponseReady

    private val _topRatedMovies = MutableLiveData<List<ResultModel>>()
    val topRatedMovies: LiveData<List<ResultModel>>
        get() = _topRatedMovies

    private val _topRatedSeries = MutableLiveData<List<ResultModel>>()
    val topRatedSeries: LiveData<List<ResultModel>>
        get() = _topRatedSeries

    private val _movieCarousel = MutableLiveData<List<ResultModel>>()
    val movieCarousel: LiveData<List<ResultModel>>
        get() = _movieCarousel

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
            enablePlaceholders = false
        )
    ) {
        moviePagingSource!!
    }.flow.cachedIn(viewModelScope)

    var searchAdapterPosition = 0
    var fetchAgain = false
    var query = ""

    fun onCreate() {
        _repositoryResponseReady.postValue(false)
        viewModelScope.launch {
            val deferreds = listOf(
                async {
                    val response = repository.moviesTopRated()
                    //MovieProvider.topRatedMovies = response?.results ?: emptyList()
                    _topRatedMovies.postValue(response?.results ?: emptyList())
                },
                async {
                    val response = repository.seriesTopRated()
                    //MovieProvider.topRatedSeries = response?.results ?: emptyList()
                    _topRatedSeries.postValue(response?.results ?: emptyList())
                },
                async {
                    val response = repository.getPopularMovies()
                    //MovieProvider.movieCarousel = response?.results ?: emptyList()
                    _movieCarousel.postValue(response?.results ?: emptyList())
                }
            )
            deferreds.awaitAll()
            _repositoryResponseReady.postValue(true)
        }
    }

    fun searchByQuery(querySearch: String) {
        query = querySearch
        cancelPagingSource()
    }

    fun cancelPagingSource() {
        moviePagingSource?.invalidate()
    }
}