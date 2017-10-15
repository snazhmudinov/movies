package com.snazhmudinov.movies.manager

import android.content.Context
import android.widget.Toast
import com.snazhmudinov.movies.constans.Constants
import com.snazhmudinov.movies.endpoints.MoviesEndPointsInterface
import com.snazhmudinov.movies.enum.Category
import com.snazhmudinov.movies.models.Cast
import com.snazhmudinov.movies.models.Movie
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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

    private fun <T> callback(func: (Response<T>?, Throwable?) -> Unit): Callback<T> {
        return object : Callback<T> {
            override fun onFailure(call: Call<T>?, t: Throwable?) {
                func(null, t)
            }

            override fun onResponse(call: Call<T>?, response: Response<T>?) {
                func(response, null)
            }
        }
    }

    fun getCast(movie: Movie, setupCast: (MutableList<Cast>) -> Unit) {
        val service = retrofit.create(MoviesEndPointsInterface::class.java)
        val call = service.getCastList(movie.id.toString(), Constants.API_KEY)

        call.enqueue(callback { response, throwable ->
            response?.let {
                if (it.isSuccessful) {
                    val actors = it.body()?.castList
                    if (actors?.isNotEmpty() == true) {
                        setupCast(
                                if (actors.size > 5) {
                                    actors.subList(0, 5)
                                } else {
                                    actors
                                }
                        )
                    }
                } else {
                    errorToast(response.errorBody().toString())
                }
            }

            throwable?.let {
                errorToast(it.message)
            }
        })
    }

    fun getTrailer(movie: Movie, playTrailer: (String) -> Unit) {
        val service = retrofit.create(MoviesEndPointsInterface::class.java)
        val call = service.getYouTubeTrailer(movie.id.toString(), Constants.API_KEY)

        call.enqueue(callback { response, throwable ->
            response?.let {
                if(it.isSuccessful) {
                    val trailer = it.body()?.results?.get(0)?.trailerURL
                    trailer?.let {
                        playTrailer(it)
                    }
                } else {
                    errorToast(it.errorBody().toString())
                }
            }

            throwable?.let {
                errorToast(it.message)
            }
        })
    }

    fun getMovies(category: String, setupMovies: (MutableList<Movie>) -> Unit, fetchSavedMovies: () -> Unit) {
        if (category == Category.favorite.name) {
            fetchSavedMovies()
        } else {
            val service = retrofit.create(MoviesEndPointsInterface::class.java)
            val call = service.getMovies(category, Constants.API_KEY)

            call.enqueue(callback { response, throwable ->
                response?.let {
                    if (it.isSuccessful) {
                        val movies = it.body()?.results
                        movies?.let {
                            setupMovies(it)
                        }
                    } else {
                        errorToast(it.errorBody().toString())
                    }
                }

                throwable?.let {
                    errorToast(it.message)
                }
            })
        }
    }

    private fun errorToast(message : String?)  { Toast.makeText(context, message, Toast.LENGTH_SHORT).show() }
}

