package com.ivandev.movieapp.ui.detail

import androidx.lifecycle.ViewModel
import com.ivandev.movieapp.domain.Repository
import com.ivandev.movieapp.domain.model.DetailModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    suspend fun searchMovieByID(id: Int, language: String): DetailModel? {
        return repository.getMovieById(id, language)
    }

    suspend fun searchSerieByID(id: Int, language: String): DetailModel? {
        return repository.getSerieById(id, language)
    }
}