package com.ivandev.movieapp.ui.main.fragment.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.ivandev.movieapp.core.adapter.Adapter
import com.ivandev.movieapp.core.adapter.FragmentAdapter
import com.ivandev.movieapp.core.common.CheckNetwork
import com.ivandev.movieapp.databinding.FragmentHomeBinding
import com.ivandev.movieapp.domain.model.ResultModel
import com.ivandev.movieapp.ui.main.MainState
import com.ivandev.movieapp.ui.main.MainViewModel
import com.ivandev.movieapp.ui.main.fragment.common.SeeDetail
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var movieAdapter: Adapter
    private lateinit var serieAdapter: Adapter
    private lateinit var viewPager2: ViewPager2
    private lateinit var movieRecyclerView: RecyclerView
    private lateinit var serieRecyclerView: RecyclerView
    private lateinit var btnNoConnection: Button
    private lateinit var noConnectionLayout: LinearLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var nestedScrollViewHome: NestedScrollView
    private var currentCoruselPosition = CURRENT_CAROUSEL_POSITION
    private var fragmentAdapter: FragmentAdapter? = null
    private var timer: Timer? = null
    private val mainVieModel: MainViewModel by activityViewModels()

    companion object {
        const val CURRENT_CAROUSEL_POSITION = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (::binding.isInitialized) {
            return binding.root
        }
        inflateView(inflater, container)
        initUI()
        initUIState()
        return binding.root
    }

    private fun inflateView(inflater: LayoutInflater, container: ViewGroup?) {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
    }

    private fun initUI() {
        setBinding()
        btnNoConnectionListener()
        setViewPager()
        setMovieAdapter()
        setSerieAdapter()
    }

    private fun setBinding() {
        viewPager2 = binding.viewPager2
        movieRecyclerView = binding.rvMovies
        serieRecyclerView = binding.rvSeries
        nestedScrollViewHome = binding.nsvHome
        btnNoConnection = binding.noConnection.btnNoConnection
        noConnectionLayout = binding.noConnection.llNoConnection
        progressBar = binding.pb
    }

    private fun initUIState() {
        lifecycleScope.launch {
            mainVieModel.mainState.collect {
                when (it) {
                    MainState.Loading -> stateLoading()
                    MainState.Finished -> stateFinished()
                }
            }
        }
    }

    private fun stateFinished() {
        hiddeProgressBar()
        checkNetwork()
    }

    private fun stateLoading() {
        showProgressBar()
        if (!CheckNetwork.isConected(requireContext())) {
            showNoConnectionLayout()
            hiddeProgressBar()
            hiddeNestedScrollViewHome()
        }
    }

    private fun btnNoConnectionListener() {
        btnNoConnection.setOnClickListener {
            noConnectionListener()
        }
    }

    private fun checkNetwork() {
        val isConnected = CheckNetwork.isConected(requireContext())
        if (isConnected) {
            setAdapters()
            createModelCarousel()
            setFragmentadapter()
            setViewPagerListener()
            initAutoSlide()
            hiddeNoConnectionLayout()
        }
    }

    private fun noConnectionListener() {
        getDataAgain()
    }

    private fun getDataAgain() {
        if (!CheckNetwork.isConected(requireContext())) return
        mainVieModel.onCreate()
        showProgressBar()
        hiddeNoConnectionLayout()
    }

    private fun setSerieAdapter() {
        serieAdapter = Adapter(
            emptyList(),
            onClickMovie = { serie ->
                SeeDetail.startDetailActivity(requireContext(), serie)
            })
        serieRecyclerView.adapter = serieAdapter
    }

    private fun setMovieAdapter() {
        movieAdapter = Adapter(
            emptyList(),
            onClickMovie = { movie ->
                SeeDetail.startDetailActivity(requireContext(), movie)
            })
        movieRecyclerView.adapter = movieAdapter
    }

    private fun setAdapters() {
        updateMovieAdapter()
        updateSerieAdapter()
    }

    private fun initAutoSlide() {
        if (mainVieModel.mainState.value is MainState.Finished) {
            hiddeProgressBar()
            showNestedScrollViewHome()
            initTimerAutoSlide()
        }
    }

    private fun showNestedScrollViewHome() {
        nestedScrollViewHome.isVisible = true
    }

    private fun hiddeNestedScrollViewHome() {
        nestedScrollViewHome.isVisible = false
    }

    private fun showProgressBar() {
        progressBar.isVisible = true
    }

    private fun hiddeProgressBar() {
        progressBar.isVisible = false
    }

    private fun showNoConnectionLayout() {
        noConnectionLayout.isVisible = true
    }

    private fun hiddeNoConnectionLayout() {
        noConnectionLayout.isVisible = false
    }

    /** Add two DataLoading.Movie object to movieList
    to model the circular carousel. */
    private fun createModelCarousel() {
        val list = mainVieModel.movieCarousel as MutableList
        val mo1 = list[0]
        val mo2 = list[1]
        list.add(mo1)
        list.add(mo2)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateSerieAdapter() {
        val list = mainVieModel.topRatedSeries
        serieAdapter.movieList = list
        serieAdapter.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateMovieAdapter() {
        val list = mainVieModel.topRatedMovies
        movieAdapter.movieList = list
        movieAdapter.notifyDataSetChanged()
    }

    private fun setFragmentadapter() {
        val list = mainVieModel.movieCarousel
        fragmentAdapter = FragmentAdapter(list, childFragmentManager, lifecycle)
        viewPager2.adapter = fragmentAdapter
    }

    private fun setViewPager() {
        with(viewPager2) {
            offscreenPageLimit = ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT
            setCurrentItem(currentCoruselPosition, false)
            isSaveEnabled = false
        }
    }

    private fun initTimerAutoSlide() {
        if (timer != null) return
        timer = Timer()
        timer!!.schedule(object : TimerTask() {
            override fun run() {
                disabledUserInputViewPager()
                viewPager2.post {
                    viewPager2.setCurrentItem(
                        currentCoruselPosition + 1,
                        true
                    )
                }
            }
        }, 8000L, 8000L)
    }

    override fun onResume() {
        super.onResume()
        val repositoryResponseReady = mainVieModel.mainState.value
        if (timer == null && repositoryResponseReady is MainState.Finished) {
            initTimerAutoSlide()
        }
    }

    override fun onPause() {
        super.onPause()
        cancelTimer()
    }

    override fun onStop() {
        super.onStop()
        cancelTimer()
    }

    override fun onDestroyView() {
        cancelTimer()
        setCurrentItemOnScreen()
        super.onDestroyView()
    }

    /** Ensure that the current page of the ViewPager2 is completely
     * set to the screen in case of a bug */
    private fun setCurrentItemOnScreen() {
        viewPager2.setCurrentItem(currentCoruselPosition + 1, false)
        viewPager2.setCurrentItem(currentCoruselPosition - 1, false)
    }

    private fun cancelTimer() {
        if (timer != null) {
            timer!!.cancel()
            timer!!.purge()
            timer = null
        }
    }

    private fun setViewPagerListener() {
        val list = mainVieModel.movieCarousel
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentCoruselPosition = position
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                if (state == ViewPager2.SCROLL_STATE_DRAGGING) {
                    cancelTimer()
                }

                if (state == ViewPager2.SCROLL_STATE_IDLE) {
                    checkViewPagerPosition(list)
                    enabledUserInputViewPager()
                    initTimerAutoSlide()
                }
            }
        })
    }

    private fun checkViewPagerPosition(list: List<ResultModel>) {
        if (currentCoruselPosition == 0) {
            viewPager2.setCurrentItem(list.size - 2, false)
        } else if (currentCoruselPosition == list.size - 1) {
            viewPager2.setCurrentItem(1, false)
        }
    }

    private fun disabledUserInputViewPager() {
        if (viewPager2.isUserInputEnabled) {
            viewPager2.isUserInputEnabled = false
        }
    }

    private fun enabledUserInputViewPager() {
        if (!viewPager2.isUserInputEnabled) {
            viewPager2.isUserInputEnabled = true
        }
    }
}