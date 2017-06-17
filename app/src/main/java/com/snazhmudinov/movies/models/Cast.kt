package com.snazhmudinov.movies.models

import com.google.gson.annotations.SerializedName

/**
 * Created by snazhmudinov on 6/17/17.
 */
class Cast constructor(castId:String, character:String, creditId:String, gender:String, id:String,
                        name:String, order:String, profilePath:String) {

    @SerializedName("cast_id")
    var castId : String? = null

    @SerializedName("character")
    var character : String? = null

    @SerializedName("credit_id")
    var creditID : String? = null

    @SerializedName("gender")
    var gender : String? = null
        get()  {
            return if (gender.equals("2")) "Male" else "Female"
        }

    @SerializedName("id")
    var id : String? = null

    @SerializedName("name")
    var name : String? = null

    @SerializedName("order")
    var order : String? = null

    @SerializedName("profile_path")
    var profilePath : String? = null
}