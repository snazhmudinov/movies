package com.snazhmudinov.movies.activities;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.snazhmudinov.movies.R;
import com.snazhmudinov.movies.adapters.MoviesAdapter;
import com.snazhmudinov.movies.application.MovieApplication;
import com.snazhmudinov.movies.constans.Constants;
import com.snazhmudinov.movies.endpoints.MoviesEndPointsInterface;
import com.snazhmudinov.movies.models.Movie;
import com.snazhmudinov.movies.models.MovieResponse;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import butterknife.BindView;
import butterknife.ButterKnife;
import icepick.Icepick;
import icepick.Icicle;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class  MovieListActivity extends AppCompatActivity {
    @Inject
    Retrofit mRetrofit;

    @BindView(R.id.drawer_layout)
    DrawerLayout mParentView;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.movies_recycler_view)
    RecyclerView mMoviesRecyclerView;

    @BindView(R.id.nav_drawer)
    NavigationView mNavView;

    private List<Movie> mFetchedMovies = new ArrayList<>();
    private SparseArray<String> mMap = new SparseArray<>();

    @Icicle String mCurrentSelection = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        Icepick.restoreInstanceState(this, savedInstanceState);

        ((MovieApplication)getApplication()).getNetworkComponents().inject(this);
        ButterKnife.bind(this);

        initLayoutManager();
        setupDrawerContent();

        //Setup categories and corresponding IDs
        initMap();

        //if nothing to restore, default the selection
        if (mCurrentSelection == null) {
            mCurrentSelection = mMap.get(R.id.action_popular);
            mNavView.setCheckedItem(R.id.action_popular);
        }

        //Fetching the movies of given category
        fetchMovies(mCurrentSelection);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    private void setupDrawerContent() {
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
        mNavView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);
                final String category = mMap.get(item.getItemId());

                mCurrentSelection = category;
                fetchMovies(category);

                mParentView.closeDrawers();
                return true;
            }
        });
    }



    private void initLayoutManager() {
        //Layout manager region
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        mMoviesRecyclerView.setLayoutManager(mLayoutManager);
    }

    private void  initAdapter() {
        //Populate & set adapter
        MoviesAdapter adapter = new MoviesAdapter(mFetchedMovies, this);
        mMoviesRecyclerView.setAdapter(adapter);
    }

    private void initMap() {
        //menu item id -> string category
        mMap.append(R.id.action_popular, "popular");
        mMap.append(R.id.action_now_playing, "now_playing");
        mMap.append(R.id.action_top_rated, "top_rated");
        mMap.append(R.id.action_upcoming, "upcoming");
    }

    private List<Movie> fetchMovies(String category) {

        MoviesEndPointsInterface service = mRetrofit.create(MoviesEndPointsInterface.class);
        Call<MovieResponse> call = service.getMovies(category, Constants.API_KEY);

        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                if (response.isSuccessful()) {
                    //First clear the previous entries
                    mFetchedMovies.clear();

                    //Get response -> List of movies
                    mFetchedMovies = response.body().getResults();

                    //Initialize adapter
                    initAdapter();

                } else {
                    Toast.makeText(MovieListActivity.this, R.string.unsuccessful_response, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                Toast.makeText(MovieListActivity.this, R.string.error_call, Toast.LENGTH_SHORT).show();
            }
        });

        return mFetchedMovies;
    }
}
