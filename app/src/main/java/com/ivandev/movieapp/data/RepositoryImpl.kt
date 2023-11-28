package com.ivandev.movieapp.data

import android.util.Log
import com.ivandev.movieapp.data.network.ApiService
import com.ivandev.movieapp.domain.Repository
import com.ivandev.movieapp.domain.model.DetailModel
import com.ivandev.movieapp.domain.model.MovieResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RepositoryImpl @Inject constructor(private val apiService: ApiService) : Repository {
    companion object {
        const val API_KEY = "2866e74fe4d2c2e7d7b08e997f990809"
    }

    override suspend fun popularMovies(): MovieResult? {
        runCatching { apiService.popularMovies(API_KEY) }
            .onSuccess { return it.toDomain() }
            .onFailure { Log.i("IvanDev", "Error: ${it.message}") }
        return null
    }

    override suspend fun moviesTopRated(): MovieResult? {
        runCatching { apiService.topRatedMovies(API_KEY) }
            .onSuccess { return it.toDomain() }
            .onFailure { Log.i("IvanDev", "Error: ${it.message}") }
        return null
    }

    override suspend fun seriesTopRated(): MovieResult? {
        runCatching { apiService.topRatedSeries(API_KEY) }
            .onSuccess { return it.toDomain() }
            .onFailure { Log.i("IvanDev", "Error: ${it.message}") }
        return null
    }

    override suspend fun searchByQuery(query: String, page: Int): List<MovieResult> {
        var response = listOf<MovieResult>()
        return withContext(Dispatchers.IO) {
            val deferredList = listOf(
                async {
                    val mov = apiService.getMovieByQuery(query, page, API_KEY).toDomain()
                    response = response.plus(mov)

                },
                async {
                    val ser = apiService.getSerieByQuery(query, page, API_KEY).toDomain()
                    response = response.plus(ser)
                })
            deferredList.awaitAll()
            response
        }
    }

    override suspend fun getMovieById(id: Int, language: String): DetailModel? {
        runCatching { apiService.getMovieByID(id, language, API_KEY) }
            .onSuccess { return it.toDomain() }
            .onFailure { Log.i("IvanDev", "Error: ${it.message}") }
        return null
    }

    override suspend fun getSerieById(id: Int, language: String): DetailModel? {
        runCatching { apiService.getSerieByID(id, language, API_KEY) }
            .onSuccess { return it.toDomain() }
            .onFailure { Log.i("IvanDev", "Error: ${it.message}") }
        return null
    }
}