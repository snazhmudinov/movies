package com.snazhmudinov.movies.activities

import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import com.snazhmudinov.movies.R
import com.snazhmudinov.movies.connectivity.Connectivity
import com.snazhmudinov.movies.connectivity.ConnectivityBroadcastReceiver
import com.snazhmudinov.movies.fragments.MoviesListFragment
import kotlinx.android.synthetic.main.activity_movie_list.*

class MovieListActivity : AppCompatActivity(), ConnectivityBroadcastReceiver.NetworkListenerInterface {

    private var moviesListFragment: MoviesListFragment? = null
    private var connectivityBroadcastReceiver: ConnectivityBroadcastReceiver? = null
    private var wasDisconnectedBefore = false

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
        connectivityBroadcastReceiver?.let { unregisterReceiver(it) }
        connectivityBroadcastReceiver = null
        super.onPause()
    }

    private fun setupDrawerContent() {
        //Setup toolbar
        setSupportActionBar(toolbar)
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

                if (!Connectivity.isNetworkAvailable(this@MovieListActivity) && !category.equals("favorite", ignoreCase = true)) {
                    Connectivity.showNoNetworkToast(this@MovieListActivity)
                    drawer_layout.closeDrawers()
                    return@OnNavigationItemSelectedListener false
                }

                it.currentSelection = category
                it.fetchMovies()
                item.isChecked = true
                drawer_layout.closeDrawers()
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
        }
        wasDisconnectedBefore = !isNetworkAvailable
    }
}
