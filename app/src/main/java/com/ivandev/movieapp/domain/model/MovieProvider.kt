package com.ivandev.movieapp.domain.model

class MovieProvider {
    companion object {
        var topRatedMovies: List<ResultModel> = emptyList()
        var topRatedSeries: List<ResultModel> = emptyList()
        var movieCarousel: List<ResultModel> = emptyList()
    }
}