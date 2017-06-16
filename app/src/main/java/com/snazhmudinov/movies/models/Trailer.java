package com.snazhmudinov.movies.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

/**
 * Created by snazhmudinov on 6/10/17.
 */
@Parcel
public class Trailer {
    @SerializedName("id")
    public Integer id;
    @SerializedName("results")
    public List<Result> results = null;

    public Trailer() {}

    public Integer getId() {
        return id;
    }

    public List<Result> getResults() {
        return results;
    }
}
