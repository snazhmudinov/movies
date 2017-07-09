package com.snazhmudinov.movies.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.snazhmudinov.movies.R
import com.snazhmudinov.movies.adapters.MoviesAdapter
import com.snazhmudinov.movies.application.MovieApplication
import com.snazhmudinov.movies.constans.Constants
import com.snazhmudinov.movies.endpoints.MoviesEndPointsInterface
import com.snazhmudinov.movies.models.Movie
import com.snazhmudinov.movies.models.MovieResponse
import kotlinx.android.synthetic.main.fragment_movies_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.util.ArrayList
import javax.inject.Inject

/**
 * Created by snazhmudinov on 7/9/17.
 */
class MoviesListFragment: Fragment() {

    @Inject lateinit var mRetrofit: Retrofit

    var currentSelection: String = ""
    var mFetchedMovies: MutableList<Movie> = ArrayList()
    val mMap = HashMap<Int, String>()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater?.inflate(R.layout.fragment_movies_list, container, false)

        (activity.application as MovieApplication).networkComponents.inject(this)

        return rootView
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        initLayoutManager()

        //Setup categories and corresponding IDs
        initMap()

        //if nothing to restore, default the selection
        if (currentSelection.isEmpty()) {
            currentSelection = mMap.get(R.id.action_popular)!!
        }

        fetchMovies(currentSelection)
    }

    fun initLayoutManager() {
        //Layout manager region
        val mLayoutManager = GridLayoutManager(context, 2)
        moviesRecyclerView.setLayoutManager(mLayoutManager)
    }

    fun initAdapter() {
        //Populate & set adapter
        val adapter = MoviesAdapter(mFetchedMovies, context)
        moviesRecyclerView.setAdapter(adapter)
    }

    fun initMap() {
        //menu item id -> string category
        mMap.put(R.id.action_popular, "popular")
        mMap.put(R.id.action_now_playing, "now_playing")
        mMap.put(R.id.action_top_rated, "top_rated")
        mMap.put(R.id.action_upcoming, "upcoming")
    }

    fun getValueFor(key: Int) = mMap.get(key)

    fun getKeyForValue(value: String): Int {
       return mMap.filterValues { it == value }
               .keys.first()
    }

    fun fetchMovies(category: String): List<Movie> {

        val service = mRetrofit.create(MoviesEndPointsInterface::class.java)
        val call = service?.getMovies(category, Constants.API_KEY)

        currentSelection = category

        call?.enqueue(object : Callback<MovieResponse> {
            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                if (response.isSuccessful) {
                    //First clear the previous entries
                    mFetchedMovies.clear()

                    //Get response -> List of movies
                    mFetchedMovies = response.body()!!.results

                    //Initialize adapter
                    initAdapter()

                } else {
                    Toast.makeText(activity, R.string.unsuccessful_response, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                Toast.makeText(activity, R.string.error_call, Toast.LENGTH_SHORT).show()
            }
        })

        return mFetchedMovies
    }
}