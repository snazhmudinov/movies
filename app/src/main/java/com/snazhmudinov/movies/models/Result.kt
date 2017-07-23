package com.snazhmudinov.movies.models

import com.google.gson.annotations.SerializedName
import com.snazhmudinov.movies.constans.Constants

import org.parceler.Parcel

/**
 * Created by snazhmudinov on 6/10/17.
 */
@Parcel
class Result {
    @SerializedName("id")
    var id: String? = null
    @SerializedName("iso_639_1")
    var iso6391: String? = null
    @SerializedName("iso_3166_1")
    var iso31661: String? = null
    @SerializedName("key")
    var key: String? = null
    @SerializedName("name")
    var name: String? = null
    @SerializedName("site")
    var site: String? = null
    @SerializedName("size")
    var size: Int? = null
    @SerializedName("type")
    var type: String? = null

    val trailerURL: String
        get() = Constants.YOUTUBE_BASE_URL + key!!

}
