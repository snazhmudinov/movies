package com.snazhmudinov.movies.fragments

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSnapHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.evernote.android.state.State
import com.evernote.android.state.StateSaver
import com.snazhmudinov.movies.R
import com.snazhmudinov.movies.activities.MovieActivity
import com.snazhmudinov.movies.adapters.MoviesAdapter
import com.snazhmudinov.movies.application.MovieApplication
import com.snazhmudinov.movies.connectivity.Connectivity
import com.snazhmudinov.movies.constans.Constants
import com.snazhmudinov.movies.database.DatabaseManager
import com.snazhmudinov.movies.enum.Category
import com.snazhmudinov.movies.manager.MovieManager
import com.snazhmudinov.movies.models.Movie
import kotlinx.android.synthetic.main.fragment_movies_list.*
import javax.inject.Inject

/**
 * Created by snazhmudinov on 7/9/17.
 */
class MoviesListFragment: Fragment(), MoviesAdapter.MovieInterface {

    @Inject lateinit var mMovieManager: MovieManager
    @Inject lateinit var mDatabaseManager: DatabaseManager

    @State var currentSelection: String = Category.popular.name
    private lateinit var dataset: MutableList<Movie>
    private lateinit var adapter: MoviesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity.application as MovieApplication).appComponents.inject(this)
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
        fetchMovies()
    }

    private fun initLayoutManager() {
        //Layout manager region
        val mLayoutManager = if (isLandscapeOrientation()) { LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false) } else { GridLayoutManager(context, 2) }
        if (isLandscapeOrientation()) {
            val snapHelper = LinearSnapHelper()
            snapHelper.attachToRecyclerView(moviesRecyclerView)
        }
        moviesRecyclerView.layoutManager = mLayoutManager
    }

    private fun populateAdapter(isLocalImage: Boolean = false) {
        //Populate & set adapter
        adapter = MoviesAdapter(dataset, context)
        adapter.mode = if (isLandscapeOrientation()) MoviesAdapter.LANDSCAPE_ORIENTATION
                            else MoviesAdapter.PORTRAIT_ORIENTATION
        toggleEmptyView(dataset.isEmpty())
        adapter.let {
            it.movieInterface = this
            it.setLocalImage(isLocalImage)
            moviesRecyclerView.adapter = it
        }
    }

    override fun onMovieSelected(movie: Movie, isLocalImage: Boolean) {
        if (!Connectivity.isNetworkAvailable(context) && !currentSelection.equals("favorite", ignoreCase = true)) {
            Connectivity.showNoNetworkToast(activity)
        } else {
            val intent = Intent(context, MovieActivity::class.java)
            intent.putExtra(Constants.MOVIE_KEY, movie)
            intent.putExtra(Constants.LOCAL_POSTER, isLocalImage)
            startActivityForResult(intent, Constants.DELETE_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode) {
            Constants.DELETE_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val movieToDelete = data?.getParcelableExtra<Movie>(Constants.MOVIE_TO_DELETE)
                    movieToDelete?.let {
                        val index = dataset.indexOf(it)
                        dataset.remove(it)
                        adapter.notifyItemRemoved(index)
                        toggleEmptyView(dataset.isEmpty())
                    }
                }
            }
        }
    }
    
    fun getCategoryForId(key: Int) = Category.values().first { it.id == key }.name

    fun getIdOfCategory(category: String) = Category.valueOf(category).id

    private fun toggleEmptyView(show: Boolean) {
        empty_rv_container?.visibility = if (show) {
            moviesRecyclerView?.visibility = View.GONE
            View.VISIBLE
        } else {
            moviesRecyclerView?.visibility = View.VISIBLE
            View.GONE
        }
    }

    fun fetchMovies() {
        mMovieManager.getMovies(currentSelection, {
            movies ->
            dataset = movies
            populateAdapter()
        }) {
            dataset = mDatabaseManager.getAllRecords()
            populateAdapter(isLocalImage = true)
        }
    }

    private fun isLandscapeOrientation() =
            resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
}