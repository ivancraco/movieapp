package com.ivandev.movieapp.core.paging

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import com.ivandev.movieapp.databinding.LoaderItemBinding

class LoaderAdapter(private val retry: () -> Unit) : LoadStateAdapter<LoaderViewHolder>() {

    override fun onBindViewHolder(holder: LoaderViewHolder, loadState: LoadState) {
        holder.bind(loadState, retry)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoaderViewHolder {
        val binging = LoaderItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return LoaderViewHolder(binging.root)
    }
}