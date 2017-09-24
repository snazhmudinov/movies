package com.snazhmudinov.movies.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import com.evernote.android.state.State
import com.evernote.android.state.StateSaver
import com.snazhmudinov.movies.R
import com.snazhmudinov.movies.activities.MovieActivity
import com.snazhmudinov.movies.activities.MovieListActivity
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
class MoviesListFragment: BaseMovieFragment(), MoviesAdapter.MovieInterface {

    @State var currentSelection: String = ""
    @State var isLandOrientation = false
    private lateinit var adapter: MoviesAdapter
    private lateinit var movies: MutableList<Movie>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StateSaver.restoreInstanceState(this, savedInstanceState)
        retainInstance = true
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        StateSaver.saveInstanceState(this, outState as Bundle)
    }

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

    private fun initLayoutManager() {
        //Layout manager region
        val mLayoutManager = GridLayoutManager(context, 2)
        moviesRecyclerView.layoutManager = mLayoutManager
    }

    fun populateAdapter(isLocalImage: Boolean = false) {
        //Populate & set adapter
        adapter = MoviesAdapter(movies, context)
        toggleEmptyView(movies.isEmpty())
        adapter.let {
            it.movieInterface = this
            it.setLocalImage(isLocalImage)
            moviesRecyclerView.adapter = it
        }
    }

    override fun onMovieSelected(movie: Movie?, isLocalImage: Boolean) {
        movie?.let {
            if (isLandOrientation) {
                val fragment = MovieFragment.newInstance(it, isLocalImage)
                (activity as MovieListActivity).displayMovie(fragment)
            } else {
                val intent = Intent(context, MovieActivity::class.java)
                intent.putExtra(Constants.MOVIE_KEY, it)
                intent.putExtra(Constants.LOCAL_POSTER, isLocalImage)
                startActivityForResult(intent, Constants.DELETE_REQUEST_CODE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode) {
            Constants.DELETE_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val movie = data?.getParcelableExtra<Movie>(Constants.MOVIE_TO_DELETE)
                    movie?.let {
                       val index = movies.indexOf(movie)
                       movies.remove(movie)
                       adapter.notifyItemRemoved(index)
                       toggleEmptyView(movies.isEmpty())
                    }
                }
            }
        }
    }
    
    fun getCategoryForId(key: Int) = Category.values().first { it.id == key }.name

    fun getIdOfCategory(category: String) = Category.valueOf(category).id

    fun fetchMovies(category: String) {
        currentSelection = category

        when(category) {
            Category.favorite.name -> {
                movies = mDatabaseManager.getAllRecords()
                populateAdapter(isLocalImage = true)
            }

            else -> {
                val service = mRetrofit.create(MoviesEndPointsInterface::class.java)
                val call = service?.getMovies(category, Constants.API_KEY)

                call?.enqueue(object : Callback<MovieResponse> {
                    override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                        if (response.isSuccessful) {
                            //Get response -> Populate the adapter
                            response.body()?.let {
                                movies = it.results
                                populateAdapter()
                            }

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

    private fun toggleEmptyView(show: Boolean) {
        empty_rv_container.visibility = if (show) {
            moviesRecyclerView.visibility = View.GONE
            View.VISIBLE
        } else {
            moviesRecyclerView.visibility = View.VISIBLE
            View.GONE
        }
    }
}