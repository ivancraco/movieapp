package com.ivandev.movieapp.ui.detail

import com.ivandev.movieapp.domain.model.DetailModel

sealed class DetailState {
    data class Success(val detailModel: DetailModel) : DetailState()
    data class Error(val error: String) : DetailState()
    object Loading : DetailState()
}
