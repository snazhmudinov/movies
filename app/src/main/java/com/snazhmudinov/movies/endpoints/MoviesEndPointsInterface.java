package com.snazhmudinov.movies.endpoints;

import com.snazhmudinov.movies.models.MovieResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by snazhmudinov on 5/28/17.
 */

public interface MoviesEndPointsInterface {

    @GET("{category}")
    Call<MovieResponse> getMovies(@Path("category") String category, @Query("api_key") String apiKey);
}
