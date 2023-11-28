package com.ivandev.movieapp.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ivandev.movieapp.core.paging.MoviePagingSource
import com.ivandev.movieapp.domain.Repository
import com.ivandev.movieapp.domain.model.ResultModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: Repository) : ViewModel() {
    private lateinit var page: Flow<PagingData<ResultModel>>
    private val _mainState = MutableStateFlow<MainState>(MainState.Loading)
    val mainState: StateFlow<MainState>
        get() = _mainState
    var topRatedMovies = listOf<ResultModel>()
    var topRatedSeries = listOf<ResultModel>()
    var movieCarousel = listOf<ResultModel>()
    var searchAdapterPosition = 0
    private var query = ""

    private var moviePagingSource: MoviePagingSource? = null
        get() {
            if (field == null || field?.invalid == true) {
                field = MoviePagingSource(repository, query)
            }
            return field
        }

    fun getPager(): Flow<PagingData<ResultModel>> {
        if (::page.isInitialized) return page
        page = Pager(
            PagingConfig(
                pageSize = 40,
                maxSize = 120,
                enablePlaceholders = false
            )
        ) {
            moviePagingSource!!
        }.flow.cachedIn(viewModelScope)
        return page
    }

    fun onCreate() {
        viewModelScope.launch {
            _mainState.value = MainState.Loading
            val deferreds = listOf(
                async {
                    val response = repository.moviesTopRated()
                    topRatedMovies = response?.results!!
                    Log.d("pepe", topRatedMovies.size.toString())
                },
                async {
                    val response = repository.seriesTopRated()
                    topRatedSeries = response?.results!!
                    Log.d("pepe", topRatedSeries.size.toString())

                },
                async {
                    val response = repository.popularMovies()
                    movieCarousel = response?.results!!
                    Log.d("pepe", movieCarousel.size.toString())

                }
            )
            deferreds.awaitAll()
            _mainState.value = MainState.Finished
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