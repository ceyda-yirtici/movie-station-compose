package com.example.movieproject.model

import com.google.gson.annotations.SerializedName

data class MovieDetail (
    @SerializedName("title") val title: String,
    @SerializedName("poster_path") val poster_path: String,
    @SerializedName("overview") val overview:String,
    @SerializedName("release_date") val release_date:String,
    @SerializedName("genre_ids") val genre_ids:List<Int>,
        )