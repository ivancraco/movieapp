package com.ivandev.movieapp.data.response

import com.google.gson.annotations.SerializedName
import com.ivandev.movieapp.data.model.DataModel
import com.ivandev.movieapp.domain.model.MovieResult

data class SerieResponse(
    @SerializedName("results") val results: List<DataModel.Serie>,
) {
    fun toDomain(): MovieResult {
        val response = results.map {
            it.toDomain()
        }
        return MovieResult(response)
    }
}
