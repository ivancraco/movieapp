package com.ivandev.movieapp.data.response

import com.google.gson.annotations.SerializedName
import com.ivandev.movieapp.data.model.DataModel
import com.ivandev.movieapp.domain.model.MovieResult

data class MovieResponse(
    @SerializedName("results") val results: List<DataModel.Movie>
) {
    fun toDomain(): MovieResult {
        val response = results.map {
            it.toDomain()
        }
        return MovieResult(response)
    }
}
