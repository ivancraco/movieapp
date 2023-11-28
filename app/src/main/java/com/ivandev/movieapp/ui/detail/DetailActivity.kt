package com.ivandev.movieapp.ui.detail

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
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
    private lateinit var noConnectionLayout: LinearLayout
    private lateinit var btnNoConnection: Button
    private lateinit var progressBar: ProgressBar
    private var searchId = 0
    private var searchModel: String? = null
    private val detailViewModel: DetailViewModel by viewModels()

    companion object {
        const val SEARCH_ID = "SEARCH_ID"
        const val SEARCH_MODEL = "SEARCH_MODEL"
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
        btnNoConnectionListener()
        initUIState()
        getIntentValues()
        getDetails()
    }

    private fun btnNoConnectionListener() {
        btnNoConnection.setOnClickListener { getDetails() }
    }

    private fun initUIState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                detailViewModel.detailState.collect {
                    when (it) {
                        DetailState.Loading -> stateLoading()
                        is DetailState.Success -> stateSuccess(it)
                        is DetailState.Error -> stateError()
                    }
                }
            }
        }
    }

    private fun stateSuccess(detailState: DetailState.Success) {
        setVisibility()
        setUI(detailState.detailModel)
    }

    private fun stateLoading() {
        progressBarVisibilityPostDelayed()
        noConnectionLayout.isVisible = false
    }

    private fun stateError() {
        setVisibility()
    }

    private fun getDetails() {
        when (searchModel) {
            MovieEnum.MOVIE.type -> {
                getDetails(searchId, getLanguage(), MovieEnum.MOVIE)
            }
            MovieEnum.SERIE.type -> {
                getDetails(searchId, getLanguage(), MovieEnum.SERIE)
            }
        }
    }

    private fun getLanguage(): String {
        val lan = Locale.getDefault().language
        val con = Locale.getDefault().country
        return String.format("%s-%s", lan, con)
    }

    private fun progressBarVisibilityPostDelayed() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (detailViewModel.detailState.value is DetailState.Loading) {
                showProgressBar()
            }
        }, 200)
    }

    private fun setBinding() {
        progressBar = binding.pb
        noConnectionLayout = binding.noConnection.llNoConnection
        btnNoConnection = binding.noConnection.btnNoConnection
    }

    private fun getIntentValues() {
        searchId = intent.getIntExtra(SEARCH_ID, 0)
        searchModel = intent.getStringExtra(SEARCH_MODEL)
    }

    private fun getDetails(id: Int, language: String, type: MovieEnum) {
        lifecycleScope.launch {
            when (type) {
                MovieEnum.MOVIE -> {
                    detailViewModel.searchMovieByID(id, language)
                }
                MovieEnum.SERIE -> {
                    detailViewModel.searchSerieByID(id, language)
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
        hiddeProgressBar()
    }

    private fun showProgressBar() {
        progressBar.isVisible = true
    }

    private fun hiddeProgressBar() {
        progressBar.isVisible = false
    }

    private fun setVisibility() {
        val stateValue = detailViewModel.detailState.value
        progressBar.isVisible = stateValue is DetailState.Loading
        noConnectionLayout.isVisible = stateValue is DetailState.Error
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