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
import kotlinx.android.synthetic.main.permission_layout.*
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
        val mLayoutManager = GridLayoutManager(context, 2)
        moviesRecyclerView.layoutManager = mLayoutManager
    }

    private fun populateAdapter(isLocalImage: Boolean = false) {
        //Populate & set adapter
        adapter = MoviesAdapter(dataset, context)
        toggleEmptyView(isReadPermissionGranted() && dataset.isEmpty())
        togglePermissionScreen()
        adapter.let {
            it.movieInterface = this
            it.setLocalImage(isLocalImage)
            moviesRecyclerView.adapter = it
        }
    }

    override fun onMovieSelected(movie: Movie, isLocalImage: Boolean) {
        if (!Connectivity.isNetworkAvailable(context) && !isFavoriteCategory()) {
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
            populateAdapter()
        }) {
            dataset = mDatabaseManager.getAllRecords()
            populateAdapter(isLocalImage = true)
        }
    }

    fun isFavoriteCategory() = currentSelection.equals("favorite", ignoreCase = true)
}

fun Context.openPermissionScreen() {
    val intent = Intent()
    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
    val uri = Uri.fromParts("package", this.packageName, null)
    intent.data = uri
    startActivity(intent)
}