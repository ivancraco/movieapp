package com.ivandev.movieapp.core.paging

import android.view.View
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.ivandev.movieapp.databinding.LoaderItemBinding

class LoaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val binding = LoaderItemBinding.bind(itemView)

    fun bind(loadState: LoadState, retry: () -> Unit) {
        binding.progressBar.isVisible = loadState is LoadState.Loading
        binding.errorText.isVisible = loadState is LoadState.Error
        binding.buttonRetry.isVisible = loadState is LoadState.Error
        binding.buttonRetry.setOnClickListener { retry() }
    }
}