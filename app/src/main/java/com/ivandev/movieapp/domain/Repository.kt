package com.ivandev.movieapp.domain

import com.ivandev.movieapp.domain.model.DetailModel
import com.ivandev.movieapp.domain.model.MovieResult

interface Repository {
    suspend fun getPopularMovies(): MovieResult?
    suspend fun seriesTopRated(): MovieResult?
    suspend fun moviesTopRated(): MovieResult?
    suspend fun searchByQuery(query: String, page: Int): List<MovieResult>
    suspend fun getMovieById(id: Int, language: String): DetailModel?
    suspend fun getSerieById(id: Int, language: String): DetailModel?
}
    