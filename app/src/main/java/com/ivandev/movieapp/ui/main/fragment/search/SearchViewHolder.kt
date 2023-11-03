package com.ivandev.movieapp.ui.main.fragment.search

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.ivandev.movieapp.R
import com.ivandev.movieapp.databinding.SearchedItemBinding
import com.ivandev.movieapp.domain.model.MovieEnum
import com.ivandev.movieapp.domain.model.ResultModel
import java.io.FileNotFoundException

class SearchViewHolder(view: View, private val onSelectedItem: (ResultModel) -> Unit) :
    RecyclerView.ViewHolder(view) {
    private val binding = SearchedItemBinding.bind(view)

    fun render(item: ResultModel?) {
        if (item == null) return

        binding.tvTitle.text = item.title
        when (item.type) {
            MovieEnum.MOVIE -> {
                binding.tvField.text = binding.tvField.context.getString(R.string.type_movie)

            }
            MovieEnum.Serie -> {
                binding.tvField.text = binding.tvField.context.getString(R.string.type_serie)
            }
        }
        val poster = item.poster_path
        if (poster.isEmpty()) {
            binding.ivCover.setImageResource(R.drawable.back_item)
        } else {
            val url = "https://image.tmdb.org/t/p/w342/${poster}"
            try {
                Glide
                    .with(binding.ivCover.context)
                    .load(url)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .into(binding.ivCover)
            } catch (fnfe: FileNotFoundException) {
                Log.d("IvanDev", "File not found")
            }
        }
        itemView.setOnClickListener { onSelectedItem(item) }
    }
}