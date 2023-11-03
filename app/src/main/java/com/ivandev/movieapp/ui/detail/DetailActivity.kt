package com.ivandev.movieapp.ui.detail

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.ivandev.movieapp.R
import com.ivandev.movieapp.data.response.MovieGenre
import com.ivandev.movieapp.databinding.ActivityDetailBinding
import com.ivandev.movieapp.domain.model.DetailModel
import com.ivandev.movieapp.domain.model.MovieEnum
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var progressBar: ProgressBar
    private val detailViewModel: DetailViewModel by viewModels()
    private var language = ""
    private var typeSearch: String? = null
    private var id = 0
    private var loading = true

    companion object {
        const val TYPESEARCH = "TYPESEARCH"
        const val SEARCH_ID = "SEARCH_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inflateView()
        setContentView(binding.root)
        initUI()
    }

    private fun inflateView() {
        binding = ActivityDetailBinding.inflate(layoutInflater)
    }

    private fun initUI() {
        setBinding()
        progressBarVisibilityPostDelayed(200)
        getIntentValues()
        setLanguage()
        getDetails()
    }

    private fun getDetails() {
        when (typeSearch) {
            MovieEnum.MOVIE.toString() -> {
                getDetails(id, language, MovieEnum.MOVIE)
            }
            MovieEnum.Serie.toString() -> {
                getDetails(id, language, MovieEnum.Serie)
            }
        }
    }

    private fun setLanguage() {
        val conName = Locale.getDefault().displayName
        language = "us-US"
        if (conName.contains("Español", true)) {
            language = "es-MX"
        }
    }

    private fun progressBarVisibilityPostDelayed(time: Long) {
        Handler(Looper.getMainLooper()).postDelayed({
            if (loading) {
                showProgressBarVisibility()
            }
        }, time)
    }

    private fun setBinding() {
        progressBar = binding.cpi
    }

    private fun getIntentValues() {
        id = intent.getIntExtra(SEARCH_ID, 0)
        typeSearch = intent.getStringExtra(TYPESEARCH)
    }

    private fun getDetails(id: Int, language: String, type: MovieEnum) {
        lifecycleScope.launch {
            when (type) {
                MovieEnum.MOVIE -> {
                    val movie = detailViewModel.searchMovieByID(id, language)
                    setUI(movie)
                }
                MovieEnum.Serie -> {
                    val serie = detailViewModel.searchSerieByID(id, language)
                    setUI(serie)
                }
            }
        }
    }

    private fun setUI(movie: DetailModel?) {
        if (movie != null) {
            val imageView = binding.ivDetail
            val genres = getGenres(movie.genres)
            seUIValues(movie.title, movie.overview, genres)
            loadItemViewImage(movie.posterPath, imageView)
        }
        hiddeProgressBarVisibility()
        loading = false
    }

    private fun showProgressBarVisibility() {
        progressBar.isVisible = true
    }

    private fun hiddeProgressBarVisibility() {
        progressBar.isVisible = false
    }

    private fun seUIValues(resTitle: String, resOverview: String, resGenres: String) {
        with(binding) {
            title.text = resTitle
            overview.text = resOverview
            genre.text = resGenres
            ivDetail.clipToOutline = true
        }
    }

    private fun loadItemViewImage(url: String, imageView: ImageView) {
        if (url.isEmpty()) {
            imageView.setImageResource(R.drawable.back_item)
        } else {
            try {
                Glide
                    .with(this@DetailActivity)
                    .load("https://image.tmdb.org/t/p/w780/${url}")
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .into(imageView)
            } catch (iae: IllegalArgumentException) {
                finish()
            }
        }
    }

    private fun getGenres(list: List<MovieGenre>): String {
        var result = ""
        list.forEach { movieGenre ->
            result = if (result.isEmpty()) {
                movieGenre.genre
            } else {
                "$result, ${movieGenre.genre}"
            }
        }
        return result
    }
}