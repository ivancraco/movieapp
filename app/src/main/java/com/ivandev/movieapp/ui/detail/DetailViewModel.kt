package com.ivandev.movieapp.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivandev.movieapp.domain.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    private val _detailState = MutableStateFlow<DetailState>(DetailState.Loading)
    val detailState: StateFlow<DetailState> = _detailState

    suspend fun searchMovieByID(id: Int, language: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _detailState.value = DetailState.Loading
            val response = repository.getMovieById(id, language)
            if (response != null) {
                _detailState.value = DetailState.Success(response)
            } else {
                _detailState.value = DetailState.Error("Error has ocurred")
            }
        }
    }

    suspend fun searchSerieByID(id: Int, language: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _detailState.value = DetailState.Loading
            val response = repository.getSerieById(id, language)
            if (response != null) {
                _detailState.value = DetailState.Success(response)
            } else {
                _detailState.value = DetailState.Error("Error has ocurred")
            }
        }
    }
}