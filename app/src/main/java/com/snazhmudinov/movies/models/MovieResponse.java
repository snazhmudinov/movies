package com.snazhmudinov.movies.models;

import com.google.auto.value.AutoValue;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by snazhmudinov on 5/28/17.
 */
public class MovieResponse {
    @SerializedName("page")
    private int page;
    @SerializedName("results")
    private List<Movie> results;
    @SerializedName("total_results")
    private int totalResults;
    @SerializedName("total_pages")
    private int totalPages;

    public MovieResponse(int page, List<Movie> results, int totalResults, int totalPages) {
        this.page = page;
        this.results = results;
        this.totalResults = totalResults;
        this.totalPages = totalPages;
    }

  /*  @SerializedName("page") public abstract int getPage();
    @SerializedName("results") public abstract List<Movie> getResults();
    @SerializedName("total_results") public abstract int getTotalResults();
    @SerializedName("total_pages") public  abstract int getTotalPages();

    public static MovieResponse create(int page, List<Movie> movies, int totalResults, int totalPages) {
        return new AutoValue_MovieResponse(page, movies, totalResults, totalPages);
    }

    public static TypeAdapter<MovieResponse> typeAdapter(Gson gson) {
        return new AutoValue_MovieResponse.GsonTypeAdapter(gson);
    }
*/
    public int getPage() { return page; }

    public List<Movie> getResults() {
        return results;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public int getTotalPages() {
        return totalPages;
    }
}
