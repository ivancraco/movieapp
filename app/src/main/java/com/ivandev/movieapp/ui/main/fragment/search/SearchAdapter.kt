package com.ivandev.movieapp.ui.main.fragment.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.ivandev.movieapp.databinding.SearchedItemBinding
import com.ivandev.movieapp.domain.model.ResultModel

class SearchAdapter(private val onSelectedItem: (ResultModel) -> Unit) :
    PagingDataAdapter<ResultModel, SearchViewHolder>(diffCallback = diffCallback) {

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.render(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val binding = SearchedItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SearchViewHolder(binding.root, onSelectedItem)
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<ResultModel>() {
            override fun areItemsTheSame(oldItem: ResultModel, newItem: ResultModel): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ResultModel, newItem: ResultModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}