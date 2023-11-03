package com.ivandev.movieapp.data.network

import com.ivandev.movieapp.data.response.MovieDetailResponse
import com.ivandev.movieapp.data.response.MovieResponse
import com.ivandev.movieapp.data.response.SerieDetailResponse
import com.ivandev.movieapp.data.response.SerieResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("movie/popular")
    suspend fun popularMovies(@Query("api_key") apiKey: String): MovieResponse

    @GET("movie/top_rated")
    suspend fun topRatedMovies(@Query("api_key") apiKey: String): MovieResponse

    @GET("tv/top_rated")
    suspend fun topRatedSeries(@Query("api_key") apiKey: String): SerieResponse

    @GET("search/movie")
    suspend fun getMovieByQuery(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("api_key") apiKey: String
    ): MovieResponse

    @GET("search/tv")
    suspend fun getSerieByQuery(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("api_key") apiKey: String
    ): SerieResponse

    @GET("movie/{id}")
    suspend fun getMovieByID(
        @Path("id") id: Int,
        @Query("language") lan: String,
        @Query("api_key") apiKey: String
    ): MovieDetailResponse

    @GET("tv/{id}")
    suspend fun getSerieByID(
        @Path("id") id: Int,
        @Query("language") lan: String,
        @Query("api_key") apiKey: String
    ): SerieDetailResponse
}