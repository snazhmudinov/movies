package com.snazhmudinov.movies.manager

import android.content.Context
import android.widget.Toast
import com.snazhmudinov.movies.constans.Constants
import com.snazhmudinov.movies.endpoints.MoviesEndPointsInterface
import com.snazhmudinov.movies.enum.Category
import com.snazhmudinov.movies.models.Cast
import com.snazhmudinov.movies.models.Movie
import com.snazhmudinov.movies.models.Trailer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.min

/**
 * Created by snazhmudinov on 10/1/17.
 */
class MovieManager(val context: Context) {

    companion object {
        val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }

    @PublishedApi
    internal fun <T> callback(func: (Response<T>?, Throwable?) -> Unit): Callback<T> {
        return object : Callback<T> {
            override fun onFailure(call: Call<T>?, t: Throwable?) {
                func(null, t)
            }

            override fun onResponse(call: Call<T>?, response: Response<T>?) {
                func(response, null)
            }
        }
    }

    inline fun getCast(movie: Movie, crossinline setupCast: (MutableList<Cast>) -> Unit) {
        val service = retrofit.create(MoviesEndPointsInterface::class.java)
        val call = service.getCastList(movie.id.toString(), Constants.API_KEY)

        call.enqueue(callback { response, throwable ->

            if (response?.isSuccessful == true) {
                val actors = response.body()?.castList ?: return@callback
                val filteredActors = actors.subList(0, min(actors.size, 5))

                if (filteredActors.isNotEmpty()) {
                    setupCast(filteredActors)
                }

            } else {
                errorToast(response?.errorBody().toString())
            }

            throwable?.let {
                errorToast(it.message)
            }
        })
    }

    inline fun getTrailer(movie: Movie, crossinline successHandler: (Trailer) -> Unit) {
        val service = retrofit.create(MoviesEndPointsInterface::class.java)
        val call = service.getYouTubeTrailer(movie.id.toString(), Constants.API_KEY)

        call.enqueue(callback { response, throwable ->

            if (response?.isSuccessful == true) {
                val trailers = response.body() ?: return@callback
                successHandler(trailers)
            } else {
                errorToast(response?.errorBody().toString())
            }

            throwable?.let {
                errorToast(it.message)
            }
        })
    }

    inline fun getMovies(category: String, crossinline setupMovies: (MutableList<Movie>) -> Unit, fetchSavedMovies: () -> Unit) {
        if (category == Category.favorite.name) {
            fetchSavedMovies()
        } else {
            val service = retrofit.create(MoviesEndPointsInterface::class.java)
            val call = service.getMovies(category, Constants.API_KEY)

            call.enqueue(callback { response, throwable ->

                if (response?.isSuccessful == true) {
                    val movies = response.body() ?: return@callback
                    setupMovies(movies.results)
                } else {
                    errorToast(response?.errorBody()?.toString())
                }

                throwable?.let {
                    errorToast(it.message)
                }
            })
        }
    }

    @PublishedApi
    internal fun errorToast(message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}

