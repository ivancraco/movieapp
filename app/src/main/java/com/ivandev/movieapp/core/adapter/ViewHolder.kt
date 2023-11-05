package com.ivandev.movieapp.core.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.ivandev.movieapp.databinding.MovieItemBinding
import com.ivandev.movieapp.domain.model.ResultModel

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val binding = MovieItemBinding.bind(view)

    fun render(movie: ResultModel) {
        binding.tvTitle.text = movie.title
        val poster = movie.poster_path
        val url = "https://image.tmdb.org/t/p/w342/${poster}"
        Glide
            .with(binding.ivPoster.context)
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .into(binding.ivPoster)
    }
}