package com.ivandev.movieapp.ui.main.fragment.common

import android.content.Context
import android.content.Intent
import com.ivandev.movieapp.domain.model.MovieEnum
import com.ivandev.movieapp.domain.model.ResultModel
import com.ivandev.movieapp.ui.detail.DetailActivity

object SeeDetail {
    fun startDetailActivity(context: Context, result: ResultModel) {
        when (result.type) {
            MovieEnum.MOVIE -> {
                startDetailActivity(context, result.id, MovieEnum.MOVIE.type)
            }
            MovieEnum.SERIE -> {
                startDetailActivity(context, result.id, MovieEnum.SERIE.type)
            }
        }
    }

    private fun startDetailActivity(context: Context, id: Int, movieEnum: String) {
        val intent = Intent(context, DetailActivity::class.java)
        intent.putExtra(DetailActivity.SEARCH_ID, id)
        intent.putExtra(DetailActivity.SEARCH_MODEL, movieEnum)
        context.startActivity(intent)
    }
}