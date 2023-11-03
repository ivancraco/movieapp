package com.ivandev.movieapp.core.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ivandev.movieapp.domain.Repository
import com.ivandev.movieapp.domain.model.ResultModel
import retrofit2.HttpException
import java.io.IOException

class MoviePagingSource(
    private val repository: Repository,
    private val query: String
) : PagingSource<Int, ResultModel>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ResultModel> {
        return try {
            if (query.isEmpty()) {
                return LoadResult.Page(
                    data = emptyList(),
                    prevKey = null,
                    nextKey = null
                )
            }
            val currentPage = params.key ?: 1
            val movieResponse = repository.getMovieByQuery(query, currentPage)
            val serieResponse = repository.getSerieByQuery(query, currentPage)
            var movieData = movieResponse?.results
            var serieData = serieResponse?.results

            var response = listOf<ResultModel>()
            if (movieData != null) {
                if (hasOddElement(movieData)) {
                    val resultModel = removeOddElement(movieData)
                    movieData = movieData.minus(resultModel)
                }
                response = response.plus(movieData)
            }
            if (serieData != null) {
                if (hasOddElement(serieData)) {
                    val resultModel = removeOddElement(serieData)
                    serieData = serieData.minus(resultModel)
                }
                response = response.plus(serieData)
            }

            LoadResult.Page(
                data = response,
                prevKey = if (currentPage == 1) null else currentPage - 1,
                nextKey = if (movieData?.isEmpty() == true) null else currentPage + 1
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (httpe: HttpException) {
            LoadResult.Error(httpe)
        }
    }

    private fun hasOddElement(movieData: List<ResultModel>): Boolean = (movieData.size % 2) != 0

    private fun removeOddElement(movieData: List<ResultModel>): ResultModel =
        movieData[movieData.size - 1]

    override fun getRefreshKey(state: PagingState<Int, ResultModel>): Int? = null
}