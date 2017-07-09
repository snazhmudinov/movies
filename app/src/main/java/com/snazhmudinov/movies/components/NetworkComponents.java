package com.snazhmudinov.movies.components;

import com.snazhmudinov.movies.activities.MovieActivity;
import com.snazhmudinov.movies.activities.MovieListActivity;
import com.snazhmudinov.movies.fragments.MoviesListFragment;
import com.snazhmudinov.movies.modules.NetworkModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by snazhmudinov on 5/28/17.
 */
@Singleton
@Component(modules = {NetworkModule.class})
public interface NetworkComponents {
    //Activities
    void inject(MovieListActivity object);
    void inject(MovieActivity object);

    //Fragments
    void inject(MoviesListFragment object);
}
