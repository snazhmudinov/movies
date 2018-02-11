package com.snazhmudinov.movies.fragments

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.evernote.android.state.State
import com.evernote.android.state.StateSaver
import com.snazhmudinov.movies.MovieListInterface
import com.snazhmudinov.movies.R
import com.snazhmudinov.movies.activities.MovieActivity
import com.snazhmudinov.movies.activities.MovieListActivity
import com.snazhmudinov.movies.adapters.MoviesAdapter
import com.snazhmudinov.movies.application.MovieApplication
import com.snazhmudinov.movies.connectivity.Connectivity
import com.snazhmudinov.movies.constans.Constants
import com.snazhmudinov.movies.database.DatabaseManager
import com.snazhmudinov.movies.enum.Category
import com.snazhmudinov.movies.manager.MovieManager
import com.snazhmudinov.movies.models.Movie
import kotlinx.android.synthetic.main.fragment_movies_list.*
import kotlinx.android.synthetic.main.permission_layout.*
import javax.inject.Inject

/**
 * Created by snazhmudinov on 7/9/17.
 */
class MoviesListFragment: Fragment(), MoviesAdapter.MovieInterface {

    @Inject lateinit var mMovieManager: MovieManager
    @Inject lateinit var mDatabaseManager: DatabaseManager

    @State var currentSelection: String = Category.popular.name
    @State var movieIndex = 0
    private lateinit var dataset: MutableList<Movie>
    private lateinit var adapter: MoviesAdapter
    private var movieListListener: MovieListInterface? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        movieListListener = context as? MovieListActivity
    }

    override fun onDetach() {
        super.onDetach()
        movieListListener = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StateSaver.restoreInstanceState(this, savedInstanceState)

        (activity.application as MovieApplication).appComponents.inject(this)

        retainInstance = true
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        StateSaver.saveInstanceState(this, outState as Bundle)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?) =
            inflater?.inflate(R.layout.fragment_movies_list, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initLayoutManager()
        fetchMovies()
    }

    private fun initLayoutManager() {
        //Layout manager region
        val mLayoutManager = if (movieListListener?.isTablet() == true) {
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        } else {
            GridLayoutManager(context, 2)
        }
        moviesRecyclerView.layoutManager = mLayoutManager
    }

    private fun populateAdapter(/*isLocalImage: Boolean = false*/) {
        //Populate & set adapter
        adapter = MoviesAdapter(dataset, context, movieListListener?.isTablet() == true)
        toggleEmptyView(isReadPermissionGranted() && dataset.isEmpty())
        togglePermissionScreen()
        adapter.let {
            it.movieInterface = this
            moviesRecyclerView.adapter = it
            it.indexOfSelectedMovie = movieIndex
            if (movieListListener?.isTablet() == true && dataset.isNotEmpty()) {
                movieListListener?.loadMovie(dataset[movieIndex])
            }
        }
    }

    override fun onMovieSelected(movie: Movie/*, isLocalImage: Boolean*/) {
        if (!Connectivity.isNetworkAvailable(context) && !isFavoriteCategory()) {
            Connectivity.showNoNetworkToast(activity)
        } else {
            if (movieListListener?.isTablet() == true) {
                if (movieIndex != dataset.indexOf(movie)) { movieListListener?.loadMovie(movie) }
            } else {
                val intent = Intent(context, MovieActivity::class.java)
                intent.putExtra(Constants.MOVIE_KEY, movie)
                startActivityForResult(intent, Constants.DELETE_REQUEST_CODE)
            }
            movieIndex = adapter.indexOfSelectedMovie
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode) {
            Constants.DELETE_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val movieToDelete = data?.getParcelableExtra<Movie>(Constants.MOVIE_TO_DELETE)
                    performDeleteMovieOperation(movieToDelete)
                }
            }
        }
    }

    fun performDeleteMovieOperation(movie: Movie?) {
        movie?.let {
            val index = dataset.indexOf(it)
            dataset.remove(it)
            adapter.notifyItemRemoved(index)
            toggleEmptyView(dataset.isEmpty())

            if (movieListListener?.isTablet() == true && dataset.isNotEmpty()) {
                adapter.indexOfSelectedMovie = 0
                movieListListener?.loadMovie(dataset[0])
            }
        }
    }
    
    fun getCategoryForId(key: Int) = Category.values().first { it.id == key }.name

    fun getIdOfCategory(category: String) = Category.valueOf(category).id

    private fun toggleEmptyView(show: Boolean) {
        movieListListener?.showEmpty(show)

        empty_rv_container?.visibility = if (show) {
            moviesRecyclerView?.visibility = View.GONE
            View.VISIBLE
        } else {
            moviesRecyclerView?.visibility = View.VISIBLE
            View.GONE
        }
    }

    private fun togglePermissionScreen() {
        if (isFavoriteCategory()) {
            permission_view.visibility = if (isReadPermissionGranted()) {
                moviesRecyclerView.visibility = View.VISIBLE
                View.GONE
            } else {
                moviesRecyclerView.visibility = View.GONE
                permission_button.setOnClickListener { context.openPermissionScreen() }
                View.VISIBLE
            }
        } else {
            moviesRecyclerView.visibility = View.VISIBLE
            permission_view.visibility = View.GONE
        }
    }

    private fun isReadPermissionGranted() =
            ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED

    fun fetchMovies() {
        mMovieManager.getMovies(currentSelection, {
            movies ->
            dataset = movies
            context?.let { populateAdapter() }
        }) {
            dataset = mDatabaseManager.getAllRecords()
            context?.let { populateAdapter() }
        }
    }

    private fun isFavoriteCategory() = currentSelection.equals("favorite", ignoreCase = true)
}

fun Context.openPermissionScreen() {
    val intent = Intent()
    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
    val uri = Uri.fromParts("package", this.packageName, null)
    intent.data = uri
    startActivity(intent)
}