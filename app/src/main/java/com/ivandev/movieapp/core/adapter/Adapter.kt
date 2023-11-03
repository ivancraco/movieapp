package com.ivandev.movieapp.core.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.ivandev.movieapp.databinding.ViewMovieItemBinding
import com.ivandev.movieapp.domain.model.ResultModel

class Adapter(
    var movieList: List<ResultModel>,
    private val onClickMovie: (ResultModel) -> Unit
) : RecyclerView.Adapter<ViewHolder>() {

    private lateinit var binding: ViewBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = ViewMovieItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movie = movieList[position]
        holder.render(movie)
        holder.itemView.setOnClickListener { onClickMovie(movie) }
    }

    override fun getItemCount(): Int = movieList.size
}
