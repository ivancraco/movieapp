package com.ivandev.movieapp.ui.main

sealed class MainState {
    object Finished : MainState()
    object Loading : MainState()
}
