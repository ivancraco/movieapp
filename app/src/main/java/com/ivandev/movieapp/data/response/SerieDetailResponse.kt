package com.ivandev.movieapp.data.response

import com.google.gson.annotations.SerializedName
import com.ivandev.movieapp.domain.model.DetailModel

data class SerieDetailResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val title: String?,
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
