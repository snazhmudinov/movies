package com.snazhmudinov.movies.activities;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.snazhmudinov.movies.R;
import com.snazhmudinov.movies.application.MovieApplication;
import com.snazhmudinov.movies.fragments.MovieFragment;
import com.snazhmudinov.movies.fragments.MoviesListFragment;
import com.snazhmudinov.movies.models.Movie;

import javax.inject.Inject;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Retrofit;

public class  MovieListActivity extends AppCompatActivity {
    @Inject
    Retrofit mRetrofit;

    @BindView(R.id.drawer_layout)
    DrawerLayout mParentView;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.nav_drawer)
    NavigationView mNavView;

    private MoviesListFragment mMoviesListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        ((MovieApplication)getApplication()).getAppComponents().inject(this);
        ButterKnife.bind(this);

        mMoviesListFragment = (MoviesListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.movies_list_fragment);
        mMoviesListFragment.setLandOrientation(findViewById(R.id.movie_fragment) != null);

        setupDrawerContent();
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

                mMoviesListFragment.fetchMovies(category);
                item.setChecked(true);
                mParentView.closeDrawers();
                return true;
            }
        });
    }

    public void displayMovie(@NonNull MovieFragment movie) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.movie_fragment, movie)
                .commit();
    }
}
