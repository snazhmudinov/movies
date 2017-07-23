package com.snazhmudinov.movies.models

import android.annotation.SuppressLint
import android.net.Uri
import com.google.gson.annotations.SerializedName
import com.snazhmudinov.movies.constans.Constants
import io.mironov.smuggler.AutoParcelable

/**
 * Created by snazhmudinov on 7/23/17.
 */
@SuppressLint("ParcelCreator")
data class Movie(@SerializedName("poster_path") val posterPath: String,
                 @SerializedName("overview") val overview: String,
                 @SerializedName("release_date") val releaseDate: String,
                 @SerializedName("id") val id: Int,
                 @SerializedName("original_title") val originalTitle: String,
                 @SerializedName("title") val title: String,
                 @SerializedName("popularity") val popularity: Double,
                 @SerializedName("vote_count") val voteCount: Int): AutoParcelable {

    val poster: Uri?
        get() = Uri.parse(Constants.POSTER_BASE_URL + posterPath)

    var trailer: String? = null
}