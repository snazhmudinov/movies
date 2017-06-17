package com.snazhmudinov.movies.models

import com.google.gson.annotations.SerializedName

/**
 * Created by snazhmudinov on 6/17/17.
 */
class CastList constructor(castList:ArrayList<Cast>) {

    @SerializedName("cast")
    var castList = ArrayList<Cast>()
}