package com.ivandev.movieapp.domain.model

import com.ivandev.movieapp.data.response.MovieGenre

data class DetailModel(
    val id: Int,
    val title: String,
    val posterPath: String,
    val overview: String,
    val genres: List<MovieGenre>
)
