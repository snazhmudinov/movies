package com.snazhmudinov.movies.activities;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import com.snazhmudinov.movies.R;
import com.snazhmudinov.movies.connectivity.Connectivity;
import com.snazhmudinov.movies.connectivity.ConnectivityBroadcastReceiver;
import com.snazhmudinov.movies.fragments.MoviesListFragment;
import butterknife.BindView;
import butterknife.ButterKnife;

public class  MovieListActivity extends AppCompatActivity implements ConnectivityBroadcastReceiver.NetworkListenerInterface {

    @BindView(R.id.drawer_layout)
    DrawerLayout mParentView;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.nav_drawer)
    NavigationView mNavView;

    private MoviesListFragment mMoviesListFragment;
    private ConnectivityBroadcastReceiver connectivityBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        ButterKnife.bind(this);

        mMoviesListFragment = (MoviesListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.movies_list_fragment);

        setupDrawerContent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerNetworkListener();
    }

    @Override
    protected void onPause() {
        if (connectivityBroadcastReceiver != null) {
            unregisterReceiver(connectivityBroadcastReceiver);
            connectivityBroadcastReceiver = null;
        }
        super.onPause();
    }

    public void setupDrawerContent() {
        //Setup toolbar
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mParentView.isDrawerOpen(Gravity.START)) {
                    mParentView.openDrawer(Gravity.START);
                }
            }
        });

        //Set click listeners to nav items
        final int currentId = mMoviesListFragment.getIdOfCategory(mMoviesListFragment.getCurrentSelection());
        mNavView.setCheckedItem(currentId);
        mNavView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                final String category = mMoviesListFragment.getCategoryForId(item.getItemId());

                if (!Connectivity.INSTANCE.isNetworkAvailable(MovieListActivity.this)
                        && !category.equalsIgnoreCase("favorite")) {
                    Connectivity.INSTANCE.showNoNetworkToast(MovieListActivity.this);
                    mParentView.closeDrawers();
                    return false;
                }

                mMoviesListFragment.setCurrentSelection(category);
                mMoviesListFragment.fetchMovies();
                item.setChecked(true);
                mParentView.closeDrawers();
                return true;
            }
        });
    }

    private void registerNetworkListener() {
        connectivityBroadcastReceiver = new ConnectivityBroadcastReceiver();
        connectivityBroadcastReceiver.setNetworkStateListener(this);
        final IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(connectivityBroadcastReceiver, filter);
    }

    @Override
    public void onNetworkStateChanged(boolean isNetworkAvailable) {
        if (isNetworkAvailable) {
            mMoviesListFragment.fetchMovies();
        }
    }
}
