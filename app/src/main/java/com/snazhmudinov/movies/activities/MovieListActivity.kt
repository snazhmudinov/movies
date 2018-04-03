package com.snazhmudinov.movies.activities

import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.View
import com.snazhmudinov.movies.MovieListInterface
import com.snazhmudinov.movies.R
import com.snazhmudinov.movies.connectivity.Connectivity
import com.snazhmudinov.movies.connectivity.ConnectivityBroadcastReceiver
import com.snazhmudinov.movies.fragments.MovieFragment
import com.snazhmudinov.movies.fragments.MoviesListFragment
import com.snazhmudinov.movies.models.Movie
import kotlinx.android.synthetic.main.activity_movie_list.*

class MovieListActivity : AppCompatActivity(),
                          ConnectivityBroadcastReceiver.NetworkListenerInterface,
                          MovieListInterface {

    private var moviesListFragment: MoviesListFragment? = null
    private var connectivityBroadcastReceiver: ConnectivityBroadcastReceiver? = null
    private var wasDisconnectedBefore = false
    private val noInternetSnackbar by lazy {
           Snackbar.make(findViewById(android.R.id.content), R.string.no_connection,
                        Snackbar.LENGTH_LONG)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_list)

        moviesListFragment = supportFragmentManager
                .findFragmentById(R.id.movies_list_fragment) as MoviesListFragment

        setupDrawerContent()
    }

    override fun onResume() {
        super.onResume()
        registerNetworkListener()
    }

    override fun onPause() {
        connectivityBroadcastReceiver?.let {
            unregisterReceiver(it)
            connectivityBroadcastReceiver = null
        }

        super.onPause()
    }

    private fun setupDrawerContent() {
        //Setup toolbar
        setSupportActionBar(toolbar)
        toolbar.setSubtitleTextAppearance(this, R.style.ToolbarSubtitle)
        toolbar.subtitle = getString(resources.getIdentifier(moviesListFragment?.currentSelection, "string", packageName))
        toolbar.setNavigationOnClickListener {
            if (!drawer_layout.isDrawerOpen(Gravity.START)) {
                drawer_layout.openDrawer(Gravity.START)
            }
        }

        //Set click listeners to nav items
        moviesListFragment?.let {
            val currentId = it.getIdOfCategory(it.currentSelection)
            nav_drawer.setCheckedItem(currentId)
            nav_drawer.setNavigationItemSelectedListener(NavigationView.OnNavigationItemSelectedListener { item ->
                val category = it.getCategoryForId(item.itemId)

                /* Don't re-fetch already shown movies */
                if (category == it.currentSelection) {
                    drawer_layout.closeDrawers()
                    return@OnNavigationItemSelectedListener false
                }

                if (!Connectivity.isNetworkAvailable(this@MovieListActivity) &&
                        !category.equals("favorite", ignoreCase = true)) {
                    Connectivity.showNoNetworkToast(this@MovieListActivity)
                    drawer_layout.closeDrawers()
                    return@OnNavigationItemSelectedListener false
                }

                it.currentSelection = category
                it.movieIndex = 0
                it.fetchMovies()
                item.isChecked = true
                drawer_layout.closeDrawers()
                toolbar.subtitle = getString(resources.getIdentifier(category, "string", packageName))
                true
            })
        }
    }

    private fun registerNetworkListener() {
        connectivityBroadcastReceiver = ConnectivityBroadcastReceiver()
        connectivityBroadcastReceiver?.let { it.networkStateListener = this }
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(connectivityBroadcastReceiver, filter)
    }

    override fun onNetworkStateChanged(isNetworkAvailable: Boolean) {
        if (isNetworkAvailable && wasDisconnectedBefore) {
            moviesListFragment?.fetchMovies()
            noInternetSnackbar.dismiss()
        }

        if (!isNetworkAvailable) { noInternetSnackbar.show() }
        wasDisconnectedBefore = !isNetworkAvailable
    }

    override fun isMasterPaneMode() = movie_fragment_container != null

    override fun loadMovie(movie: Movie) {
        val isFavorite = moviesListFragment?.currentSelection.equals(getString(R.string.favorite), true)
        supportFragmentManager.beginTransaction()
                .replace(R.id.movie_fragment_container, MovieFragment.newInstance(movie, isFavorite))
                .commit()
    }

    override fun showEmpty(show: Boolean) {
        movie_fragment_container?.visibility = if (show) View.GONE else View.VISIBLE
    }

    override fun onDeleteMovie(movie: Movie) {
        moviesListFragment?.performDeleteMovieOperation(movie)
    }
}