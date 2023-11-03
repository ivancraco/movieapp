package com.ivandev.movieapp.data

import android.util.Log
import com.ivandev.movieapp.data.network.ApiService
import com.ivandev.movieapp.domain.Repository
import com.ivandev.movieapp.domain.model.DetailModel
import com.ivandev.movieapp.domain.model.MovieResult
import javax.inject.Inject

class RepositoryImpl @Inject constructor(private val apiService: ApiService) : Repository {

    override suspend fun getPopularMovies(): MovieResult? {
        runCatching { apiService.popularMovies("2866e74fe4d2c2e7d7b08e997f990809") }
            .onSuccess { return it.toDomain() }
            .onFailure { Log.i("IvanDev", "Error: ${it.message}") }
        return null
    }

    override suspend fun moviesTopRated(): MovieResult? {
        runCatching { apiService.topRatedMovies("2866e74fe4d2c2e7d7b08e997f990809") }
            .onSuccess { return it.toDomain() }
            .onFailure { Log.i("IvanDev", "Error: ${it.message}") }
        return null
    }

    override suspend fun seriesTopRated(): MovieResult? {
        runCatching { apiService.topRatedSeries("2866e74fe4d2c2e7d7b08e997f990809") }
            .onSuccess { return it.toDomain() }
            .onFailure { Log.i("IvanDev", "Error: ${it.message}") }
        return null
    }

    override suspend fun getMovieByQuery(query: String, page: Int): MovieResult? {
        runCatching { apiService.getMovieByQuery(query, page, "2866e74fe4d2c2e7d7b08e997f990809") }
            .onSuccess { return it.toDomain() }
            .onFailure { Log.i("IvanDev", "Error: ${it.message}") }
        return null
    }

    override suspend fun getSerieByQuery(query: String, page: Int): MovieResult? {
        runCatching { apiService.getSerieByQuery(query, page, "2866e74fe4d2c2e7d7b08e997f990809") }
            .onSuccess { return it.toDomain() }
            .onFailure { Log.i("IvanDev", "Error: ${it.message}") }
        return null
    }

    override suspend fun getMovieById(id: Int, language: String): DetailModel? {
        runCatching { apiService.getMovieByID(id, language, "2866e74fe4d2c2e7d7b08e997f990809") }
            .onSuccess { return it.toDomain() }
            .onFailure { Log.i("IvanDev", "Error: ${it.message}") }
        return null
    }

    override suspend fun getSerieById(id: Int, language: String): DetailModel? {
        runCatching { apiService.getSerieByID(id, language, "2866e74fe4d2c2e7d7b08e997f990809") }
            .onSuccess { return it.toDomain() }
            .onFailure { Log.i("IvanDev", "Error: ${it.message}") }
        return null
    }
}