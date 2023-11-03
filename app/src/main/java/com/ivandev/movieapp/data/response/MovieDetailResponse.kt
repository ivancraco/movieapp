package com.ivandev.movieapp.data.response

import com.google.gson.annotations.SerializedName
import com.ivandev.movieapp.domain.model.DetailModel

data class MovieDetailResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("overview") val overview: String?,
    @SerializedName("genres") val genres: List<MovieGenre>?
) {
    fun toDomain(): DetailModel {
        return DetailModel(
            id,
            title ?: "",
            posterPath ?: "",
            overview ?: "",
            genres ?: emptyList()
        )
    }
}