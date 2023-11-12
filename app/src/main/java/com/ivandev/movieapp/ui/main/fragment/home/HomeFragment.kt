package com.ivandev.movieapp.ui.main.fragment.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.ivandev.movieapp.core.adapter.Adapter
import com.ivandev.movieapp.core.adapter.FragmentAdapter
import com.ivandev.movieapp.core.common.CheckNetwork
import com.ivandev.movieapp.databinding.FragmentHomeBinding
import com.ivandev.movieapp.domain.model.ResultModel
import com.ivandev.movieapp.ui.main.MainViewModel
import com.ivandev.movieapp.ui.main.fragment.common.SeeDetail
import dagger.hilt.android.AndroidEntryPoint
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
    private val mainVieModel: MainViewModel by activityViewModels()
    private var currentCoruselPosition = CURRENT_CAROUSEL_POSITION
    private var fragmentAdapter: FragmentAdapter? = null
    private var timer: Timer? = null

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
        checkNetwork()
    }

    private fun btnNoConnectionListener() {
        btnNoConnection.setOnClickListener {
            noConnectionListener()
        }
    }

    private fun checkNetwork() {
        val isConnected = CheckNetwork.isConected(requireContext())
        if (isConnected) {
            dataResponseObserver()
            binding.noConnection.llNoConnection.isVisible = false
        } else {
            binding.nsvMain.visibility = View.INVISIBLE
            binding.noConnection.llNoConnection.isVisible = true
        }
    }

    private fun noConnectionListener() {
        checkNetwork()
    }

    private fun setBinding() {
        viewPager2 = binding.viewPager2
        movieRecyclerView = binding.rvMovies
        serieRecyclerView = binding.rvSeries
        btnNoConnection = binding.noConnection.btnNoConnection
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

    private fun dataResponseObserver() {
        mainVieModel.onCreate()
        binding.pb.isVisible = true
        movieObserver()
        serieObserver()
        movieCarouselObserver()
        responseReadyObserver()
    }

    private fun responseReadyObserver() {
        mainVieModel.repositoryResponseReady.observe(viewLifecycleOwner) {
            if (it) {
                binding.pb.isVisible = false
                binding.nsvMain.isVisible = true
                initTimerAutoSlide()
                mainVieModel.repositoryResponseReady.removeObservers(viewLifecycleOwner)
            }
        }
    }

    private fun movieCarouselObserver() {
        mainVieModel.movieCarousel.observe(viewLifecycleOwner) { resultModelList ->
            if (resultModelList.isNotEmpty()) {
                createModelCarousel(resultModelList as MutableList<ResultModel>)
                setFragmentadapter(resultModelList)
                setViewPagerListener(resultModelList)
                mainVieModel.movieCarousel.removeObservers(viewLifecycleOwner)
            }
        }
    }

    private fun serieObserver() {
        mainVieModel.topRatedSeries.observe(viewLifecycleOwner) {
            updateSerieAdapter(it)
        }
    }

    private fun movieObserver() {
        mainVieModel.topRatedMovies.observe(viewLifecycleOwner) {
            updateMovieAdapter(it)
        }
    }

    /** Add two DataLoading.Movie object to movieList
    to model the circular carousel. */
    private fun createModelCarousel(list: MutableList<ResultModel>) {
        val mo1 = list[0]
        val mo2 = list[1]
        list.add(mo1)
        list.add(mo2)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateSerieAdapter(resultModels: List<ResultModel>) {
        serieAdapter.movieList = resultModels
        serieAdapter.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateMovieAdapter(resultModels: List<ResultModel>) {
        movieAdapter.movieList = resultModels
        movieAdapter.notifyDataSetChanged()
    }

    private fun setFragmentadapter(mv: List<ResultModel>) {
        fragmentAdapter = FragmentAdapter(mv, childFragmentManager, lifecycle)
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
        val repositoryResponseReady = mainVieModel.repositoryResponseReady.value
        if (timer == null && repositoryResponseReady == true) {
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
        viewPager2.setCurrentItem(currentCoruselPosition - 1, false)
        viewPager2.setCurrentItem(currentCoruselPosition + 1, false)
    }

    private fun cancelTimer() {
        if (timer != null) {
            timer!!.cancel()
            timer!!.purge()
            timer = null
        }
    }

    private fun setViewPagerListener(list: List<ResultModel>) {
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