package com.snazhmudinov.movies.models

import com.google.gson.annotations.SerializedName

import org.parceler.Parcel

/**
 * Created by snazhmudinov on 6/10/17.
 */
@Parcel
class Trailer {
    @SerializedName("id")
    var id: Int? = null
    @SerializedName("results")
    var results: List<Result>? = null
}
