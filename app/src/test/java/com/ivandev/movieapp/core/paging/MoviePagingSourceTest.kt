package com.ivandev.movieapp.core.paging

import androidx.paging.PagingSource
import com.ivandev.movieapp.data.RepositoryImpl
import com.ivandev.movieapp.domain.model.ResultModel
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
internal class MoviePagingSourceTest {

    @RelaxedMockK
    private lateinit var repository: RepositoryImpl

    private lateinit var pagingSource: MoviePagingSource

    @Before
    fun onBefore() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        pagingSource = MoviePagingSource(repository, "a")
    }

    @Test
    fun `paging source load empty list when repository return empty list`() = runTest {
        coEvery { repository.searchByQuery(any(), any()) } returns emptyList()

        val expectedResult = PagingSource.LoadResult.Page<Int, ResultModel>(
            emptyList(),
            null,
            null
        )

        assertEquals(
            expectedResult.toString(),
            pagingSource.load(
                PagingSource.LoadParams.Refresh(
                    key = 1,
                    loadSize = 1,
                    placeholdersEnabled = false
                )
            ).toString()
        )
    }
}