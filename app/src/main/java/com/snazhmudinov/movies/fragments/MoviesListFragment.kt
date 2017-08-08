package com.snazhmudinov.movies.fragments

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.snazhmudinov.movies.R
import com.snazhmudinov.movies.adapters.MoviesAdapter
import com.snazhmudinov.movies.constans.Constants
import com.snazhmudinov.movies.endpoints.MoviesEndPointsInterface
import com.snazhmudinov.movies.enum.Category
import com.snazhmudinov.movies.models.Movie
import com.snazhmudinov.movies.models.MovieResponse
import kotlinx.android.synthetic.main.fragment_movies_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by snazhmudinov on 7/9/17.
 */
class MoviesListFragment: BaseMovieFragment() {

    var currentSelection: String = ""
    var mFetchedMovies: MutableList<Movie> = ArrayList()
    private val mMap = HashMap<Int, String>()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?) =
            inflater?.inflate(R.layout.fragment_movies_list, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        initLayoutManager()

        //if nothing to restore, default the selection
        if (currentSelection.isEmpty()) {
            currentSelection = Category.popular.name
        }

        fetchMovies(currentSelection)
    }

    fun initLayoutManager() {
        //Layout manager region
        val mLayoutManager = GridLayoutManager(context, 2)
        moviesRecyclerView.layoutManager = mLayoutManager
    }

    fun populateAdapter(movies: MutableList<Movie>) {
        //Populate & set adapter
        val adapter = MoviesAdapter(movies, context)
        moviesRecyclerView.adapter = adapter
    }

    fun getCategoryForId(key: Int): String {
        return Category.values().filter {
            it.id == key
        }.first().name

    }

    fun getIdOfCategory(category: String) = Category.valueOf(category).id

    fun fetchMovies(category: String) {

        when(category) {
            Category.favorite.name -> {
                if (mDatabaseManager.tableHasRecords()) {
                    populateAdapter(mDatabaseManager.getAllRecords())
                }
            }

            else -> {
                val service = mRetrofit.create(MoviesEndPointsInterface::class.java)
                val call = service?.getMovies(category, Constants.API_KEY)

                currentSelection = category

                call?.enqueue(object : Callback<MovieResponse> {
                    override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                        if (response.isSuccessful) {
                            //Get response -> List of movies
                            response.body()?.let { populateAdapter(it.results) }

                        } else {
                            Toast.makeText(activity, R.string.unsuccessful_response, Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                        Toast.makeText(activity, R.string.error_call, Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }
}