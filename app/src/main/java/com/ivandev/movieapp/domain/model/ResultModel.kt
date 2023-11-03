package com.ivandev.movieapp.domain.model

data class ResultModel(
    val id: Int,
    val poster_path: String,
    val title: String,
    val type: MovieEnum
)
