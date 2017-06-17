package com.snazhmudinov.movies.models;

import com.google.gson.annotations.SerializedName;
import com.snazhmudinov.movies.constans.Constants;

import org.parceler.Parcel;

/**
 * Created by snazhmudinov on 6/10/17.
 */
@Parcel
public class Result {
    @SerializedName("id")
    public String id;
    @SerializedName("iso_639_1")
    public String iso6391;
    @SerializedName("iso_3166_1")
    public String iso31661;
    @SerializedName("key")
    public String key;
    @SerializedName("name")
    public String name;
    @SerializedName("site")
    public String site;
    @SerializedName("size")
    public Integer size;
    @SerializedName("type")
    public String type;

    public Result() {}

    public String getId() {
        return id;
    }

    public String getIso6391() {
        return iso6391;
    }

    public String getIso31661() {
        return iso31661;
    }

    public String getKey() {
        return Constants.YOUTUBE_BASE_URL + key;
    }

    public String getName() {
        return name;
    }

    public String getSite() {
        return site;
    }

    public Integer getSize() {
        return size;
    }

    public String getType() {
        return type;
    }

}
