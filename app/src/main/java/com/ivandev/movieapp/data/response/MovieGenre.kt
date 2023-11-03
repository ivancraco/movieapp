package com.ivandev.movieapp.data.response

import com.google.gson.annotations.SerializedName

class MovieGenre(
    @SerializedName("name") val genre: String
)