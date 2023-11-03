package com.ivandev.movieapp.data.model

import com.google.gson.annotations.SerializedName
import com.ivandev.movieapp.domain.model.MovieEnum
import com.ivandev.movieapp.domain.model.ResultModel

sealed class DataModel {
    data class Movie(
        @SerializedName("id") val id: Int,
        @SerializedName("poster_path") val poster_path: String,
        @SerializedName("title") val title: String
    ) : DataModel() {
        fun toDomain(): ResultModel {
            return ResultModel(
                id = id,
                poster_path = poster_path,
                title = title,
                type = MovieEnum.MOVIE
            )
        }
    }

    data class Serie(
        @SerializedName("id") val id: Int,
        @SerializedName("poster_path") val poster_path: String,
        @SerializedName("name") val title: String
    ) : DataModel() {
        fun toDomain(): ResultModel {
            return ResultModel(
                id = id,
                poster_path = poster_path,
                title = title,
                type = MovieEnum.Serie
            )
        }
    }
}