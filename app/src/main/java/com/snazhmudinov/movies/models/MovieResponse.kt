package com.snazhmudinov.movies.models

import com.google.gson.annotations.SerializedName

/**
 * Created by snazhmudinov on 5/28/17.
 */
class MovieResponse(@SerializedName("page") val page: Int,
                    @SerializedName("results") val results: MutableList<Movie>,
                    @SerializedName("total_results") val totalResults: Int,
                    @SerializedName("total_pages") val totalPages: Int)
