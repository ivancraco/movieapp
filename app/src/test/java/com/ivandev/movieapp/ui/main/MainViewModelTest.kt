package com.ivandev.movieapp.ui.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.ivandev.movieapp.data.RepositoryImpl
import com.ivandev.movieapp.domain.model.MovieEnum
import com.ivandev.movieapp.domain.model.MovieResult
import com.ivandev.movieapp.domain.model.ResultModel
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class MainViewModelTest {
    @MockK
    private lateinit var repository: RepositoryImpl

    private lateinit var mainViewModel: MainViewModel

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun onBefore() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        mainViewModel = MainViewModel(repository)
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @Test
    fun `onCreate method should get all default movies and series`() = runTest {
        val movieTopRated = listOf(ResultModel(1, "", "", MovieEnum.MOVIE))
        val serieTopRated = listOf(ResultModel(1, "", "", MovieEnum.SERIE))
        val movieCarousel = listOf(ResultModel(1, "", "", MovieEnum.MOVIE))

        coEvery { repository.moviesTopRated() } returns MovieResult(movieTopRated)
        coEvery { repository.seriesTopRated() } returns MovieResult(serieTopRated)
        coEvery { repository.popularMovies() } returns MovieResult(movieCarousel)

        mainViewModel.onCreate()

        assert(mainViewModel.topRatedMovies == movieTopRated)
        assert(mainViewModel.topRatedSeries == serieTopRated)
        assert(mainViewModel.movieCarousel == movieCarousel)
    }

    @Test
    fun `onCreate method should get all empty lists with repository return null`() = runTest {
        val emptyList = listOf<ResultModel>()

        coEvery { repository.moviesTopRated() } returns null
        coEvery { repository.seriesTopRated() } returns null
        coEvery { repository.popularMovies() } returns null

        mainViewModel.onCreate()

        assert(mainViewModel.topRatedMovies == emptyList)
        assert(mainViewModel.topRatedSeries == emptyList)
        assert(mainViewModel.movieCarousel == emptyList)
    }

    @After
    fun onAfter() {
        Dispatchers.resetMain()
    }
}