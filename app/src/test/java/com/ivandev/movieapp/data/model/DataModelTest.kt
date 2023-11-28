package com.ivandev.movieapp.data.model

import com.ivandev.movieapp.domain.model.MovieEnum
import io.kotlintest.shouldBe
import org.junit.Test

internal class DataModelTest {
    @Test
    fun `movie model should return correct result model`() {
        val movie = DataModel.Movie(1, "", "")

        val result = movie.toDomain()

        result.id shouldBe movie.id
        result.type shouldBe MovieEnum.MOVIE
    }

    @Test
    fun `serie model should return correct result model`() {
        val serie = DataModel.Serie(1, "", "")

        val result = serie.toDomain()

        result.id shouldBe serie.id
        result.type shouldBe MovieEnum.SERIE
    }
}