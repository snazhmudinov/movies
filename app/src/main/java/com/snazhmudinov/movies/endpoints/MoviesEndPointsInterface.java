package com.snazhmudinov.movies.endpoints;

import com.snazhmudinov.movies.models.CastList;
import com.snazhmudinov.movies.models.MovieResponse;
import com.snazhmudinov.movies.models.Trailer;

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

    @GET("{id}/videos")
    Call<Trailer> getYouTubeTrailer(@Path("id") String id, @Query("api_key") String apiKey);

    @GET("{id}/credits")
    Call<CastList> getCastList(@Path("id") String id, @Query("api_key") String apiKey);
}
