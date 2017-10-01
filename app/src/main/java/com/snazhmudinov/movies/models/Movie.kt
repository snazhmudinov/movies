package com.snazhmudinov.movies.models

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.snazhmudinov.movies.constans.Constants

/**
 * Created by snazhmudinov on 7/23/17.
 */
data class Movie(@SerializedName("poster_path") var posterPath: String,
                 @SerializedName("overview") val overview: String,
                 @SerializedName("release_date") val releaseDate: String,
                 @SerializedName("id") val id: Int,
                 @SerializedName("original_title") val originalTitle: String,
                 @SerializedName("title") val title: String,
                 @SerializedName("popularity") val popularity: Double,
                 @SerializedName("vote_count") val voteCount: Int): Parcelable {

    val webPosterPath: Uri?
        get() = Uri.parse(Constants.POSTER_BASE_URL + posterPath)

    var savedFilePath: String? = null

    var trailer: String? = null

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readDouble(),
            parcel.readInt()) {
        savedFilePath = parcel.readString()
        trailer = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(posterPath)
        parcel.writeString(overview)
        parcel.writeString(releaseDate)
        parcel.writeInt(id)
        parcel.writeString(originalTitle)
        parcel.writeString(title)
        parcel.writeDouble(popularity)
        parcel.writeInt(voteCount)
        parcel.writeString(savedFilePath)
        parcel.writeString(trailer)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<Movie> {
        override fun createFromParcel(parcel: Parcel) = Movie(parcel)

        override fun newArray(size: Int): Array<Movie?> = arrayOfNulls(size)
    }
}