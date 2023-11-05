package com.ivandev.movieapp.ui.main.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.ivandev.movieapp.databinding.FragmentCoruselBinding
import com.ivandev.movieapp.domain.model.ResultModel
import com.ivandev.movieapp.ui.main.fragment.common.SeeDetail
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CoruselFragment(val movie: ResultModel) : Fragment() {
    private var arg: Int? = null
    private lateinit var binding: FragmentCoruselBinding

    companion object {
        private const val ARG_PARAM = "PARAM"

        @JvmStatic
        fun newInstance(movie: ResultModel) =
            CoruselFragment(movie)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            arg = it.getInt(ARG_PARAM)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        inflateView(inflater, container)
        initUI()
        return binding.root
    }

    private fun initUI() {
        val url = "https://image.tmdb.org/t/p/w780/${movie.poster_path}"
        binding.tvCarousel.text = movie.title
        Glide.with(binding.ivCarousel.context)
            .load(url)
            .into(binding.ivCarousel)
        binding.btnCarousel.setOnClickListener { seeDetail(requireContext(), movie) }
    }

    private fun inflateView(inflater: LayoutInflater, container: ViewGroup?) {
        binding = FragmentCoruselBinding.inflate(inflater, container, false)
    }

    private fun seeDetail(context: Context, movie: ResultModel) {
        SeeDetail.startDetailActivity(context, movie)
    }
}